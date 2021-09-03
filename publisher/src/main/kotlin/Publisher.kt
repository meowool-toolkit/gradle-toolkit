@file:Suppress("NAME_SHADOWING")

package com.meowool.toolkit.gradle

import MavenLocalTarget
import PublishingTarget
import SonatypeTarget
import addIfNotExists
import com.android.build.gradle.BaseExtension
import com.gradle.publish.PluginBundleExtension
import com.gradle.publish.PublishPlugin
import com.meowool.sweekt.safeCast
import groovy.util.Node
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.runBlocking
import org.gradle.BuildAdapter
import org.gradle.BuildResult
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.publishing
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * A plugin for publishing Gradle or Maven publications.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class Publisher : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    extensions.addIfNotExists("publication") { PublicationExtension(project) }
    afterEvaluate {
      val extension = extensions.getByType<PublicationExtension>()

      // Incompatible project do not enable publishing
      if (isCompatible.not()) {
        if (extension.showIncompatibleWarnings) println("WARN: Publisher skips incompatible project: $name")
        return@afterEvaluate
      }

      apply<MavenPublishPlugin>()
      apply<DokkaPlugin>()

      if (extension.target.isEmpty()) {
        extension.publishToSonatype()
      }

      publishing {
        publishComponentsAndSourcesJar(project)
        configureData(extension)
        configureDokkaJar(extension)
        configureSignings(extension)
        configureRepositories(extension)
      }
      configureGradlePlugin(extension)

      // Sonatype
      initSonatypeBuild()
      extension.target.filterIsInstance<SonatypeTarget>().firstOrNull()?.let {
        taskInitializeSonatypeStaging(it)
        taskCloseAndReleaseSonatypeRepository(it)
      }
    }
  }

  private fun Project.configureGradlePlugin(extension: PublicationExtension) = with(extension) {
    // Publish gradle plugin only when `pluginClass` is set
    if (pluginClass != null) {
      apply<PublishPlugin>()
      extensions.configure<PluginBundleExtension> {
        tags = data.tagsOrDefault()
        data.urlOrDefault()?.let(::setWebsite)
        data.vcsOrDefault()?.let(::setVcsUrl)
        project.description?.let(::setDescription)
      }
      extensions.configure<GradlePluginDevelopmentExtension> {
        plugins.create(data.artifactIdOrDefault()) {
          id = data.pluginIdOrDefault()
          implementationClass = pluginClass
          displayName = data.displayNameOrDefault()
          project.description?.let(::setDescription)
        }
      }
    }
  }

  /**
   * Configures the project components and sources jar to publish.
   */
  private fun PublishingExtension.publishComponentsAndSourcesJar(project: Project) = with(project) {
    // The kotlin multiplatform plugin publishes its own components
    if (extensions.findByType<KotlinMultiplatformExtension>() != null) return@with

    val androidExtension = extensions.findByName("android") as? BaseExtension
    val isAndroid = androidExtension != null
    val kotlinSourcesJar = tasks.findByName("kotlinSourcesJar")
    val sourcesJar = when {
      isAndroid -> createSourcesJar("androidSourcesJar") { from(androidExtension!!.sourceSets.getByName("main").java.srcDirs) }
      // Hand it over to kotlin
      kotlinSourcesJar != null -> kotlinSourcesJar
      else -> createSourcesJar("javaSourcesJar") {
        from(extensions.getByType<JavaPluginExtension>().sourceSets.getByName("main").allSource)
      }
    }

    // Configure artifacts with sources jar
    when {
      isAndroid -> androidExtension!!.configureAllVariants { variant ->
        publications.create<MavenPublication>(variant.name) {
          from(components[variant.name])
          artifact(sourcesJar)
        }
      }
      else -> publications.create<MavenPublication>("maven") {
        components.findByName("kotlin")?.let(::from) ?: components.findByName("java")?.let(::from)
        artifact(sourcesJar)
      }
    }
  }

  /**
   * Configures the data of publication.
   */
  private fun PublishingExtension.configureData(extension: PublicationExtension) = with(extension) {
    project.group = data.groupIdOrDefault()
    project.version = data.versionOrDefault()
    project.description = data.descriptionOrDefault()

    target.forEach {
      it.project = project
      it.isSnapshot = project.version.toString().isSnapshot
    }

    project.afterEvaluate {
      publications.withType<MavenPublication> {
        groupId = project.group.toString()
        version = project.version.toString()
        // Ref: https://github.com/vanniktech/gradle-maven-publish-plugin/blob/a824079592fd0e1895aa0b293b798f593949fadb/plugin/src/main/kotlin/com/vanniktech/maven/publish/legacy/Coordinates.kt#L25
        if (this@withType.name.endsWith("PluginMarkerMaven").not()) {
          // There will be a suffix when a multi-platform project, so use the way of prefix replacing.
          artifactId = data.artifactIdOrDefault() + artifactId.removePrefix(project.name)
        }
        pom {
          name.set(data.displayNameOrDefault())
          url.set(data.urlOrDefault())
          description.set(project.description.toString())

          developers {
            data.developersOrDefault().forEach {
              developer {
                it.id?.let(id::set)
                it.name?.let(name::set)
                it.email?.let(email::set)
                it.url?.let(url::set)
              }
            }
          }
          licenses {
            data.licensesOrDefault().forEach {
              license {
                it.name?.let(name::set)
                it.url?.let(url::set)
              }
            }
          }
          organization {
            name.set(data.organizationNameOrDefault())
            name.set(data.organizationUrlOrDefault())
          }
          scm {
            url.set(data.vcsOrDefault())
          }

          // Collect all the repositories used in the project to add to the POM
          // See https://maven.apache.org/pom.html#Repositories
          withXml {
            project.repositories
              .filterIsInstance<MavenArtifactRepository>()
              .filterNot { it.url.host.contains("repo.maven.apache.org") }
              .takeIf { it.isNotEmpty() }
              ?.also { repositories ->
                val xml = this.asNode()
                // Find or create `repositories` node
                val repositoriesNode = xml.children().firstOrNull { it is Node && it.name() == "repositories" } as? Node
                  ?: xml.appendNode("repositories")

                repositories.forEach { repo ->
                  repositoriesNode?.appendNode("repository")?.apply {
                    appendNode("id", repo.name)
                    appendNode("url", repo.url.toString())
                  }
                }
              }
          }

          data.pomConfigurations.forEach { it() }
        }
      }
    }
  }

  /**
   * Configures the Dokka jar to publish.
   */
  private fun PublishingExtension.configureDokkaJar(extension: PublicationExtension) = with(extension) {
    val dokkaJar by project.tasks.register<Jar>("dokkaJar") {
      val dokkaTask = project.tasks.findByName(dokkaFormat.taskName).safeCast()
        ?: project.tasks.withType<DokkaTask>().first()

      dependsOn(dokkaTask)
      from(dokkaTask.outputDirectory)
      archiveClassifier.set("javadoc")
    }
    // Configures dokka jar to all publications
    publications.withType<MavenPublication> { artifact(dokkaJar) }
  }

  /**
   * Configures the signings to sign artifacts to publish.
   */
  private fun PublishingExtension.configureSignings(extension: PublicationExtension) = with(extension) {
    if (isSignRelease || isSignSnapshot) {
      project.apply<SigningPlugin>()

      // Signing the artifacts
      project.extensions.configure<SigningExtension> {
        setRequired({
          when {
            isSignRelease && isSignSnapshot.not() -> project.version.toString().isSnapshot.not()
            isSignSnapshot && isSignRelease.not() -> project.version.toString().isSnapshot
            isSignRelease && isSignSnapshot -> true
            else -> false
          }
        })
        if (isRequired) sign(publications)
      }
    }
  }

  /**
   * Configures target repositories to publish artifacts.
   */
  private fun PublishingExtension.configureRepositories(extension: PublicationExtension) = repositories {
    extension.target.forEach {
      val url = it.url
      val maven = if (url == MavenLocalTarget::class) mavenLocal() else maven(it.url)
      if (it.requiredCertificate) maven.credentials(PasswordCredentials::class.java)
    }
  }

  private fun Project.initSonatypeBuild() {
    // Listening to build finished and to close repositories, only need to add to root project once
    if (buildListener.not()) {
      buildListener = true
      rootProject.gradle.addBuildListener(object : BuildAdapter() {
        override fun buildFinished(result: BuildResult) {
          super.buildFinished(result)
          runBlocking {
            @Suppress("UNCHECKED_CAST")
            repositoriesToClose.forEach { (baseUrl, repositoryIds) ->
              val client = rootProject.createNexusStagingClient(baseUrl)
              repositoryIds.asFlow().map {
                val id = listOf(it)
                println("Closing $it")
                client.closeRepositories(id)
                println("Releasing $it")
                client.releaseRepositories(id, dropAfterRelease = true)
              }.retry(retries = 5) {
                // Retry after 1000 ms
                delay(1000)
                true
              }.collect()
            }
            repositoriesToClose.clear()
          }
        }
      })
    }
  }

  /**
   * Configures the task used to initialize the Sonatype staging repository.
   *
   * Because the Sonatype release version needs to specify the staging repository.
   */
  private fun Project.taskInitializeSonatypeStaging(target: SonatypeTarget) =
    tasks.withType<PublishToMavenRepository>().all {
      doFirst {
        // Don't redirect the publish of snapshot artifacts
        if (target.isSnapshot.not()) runBlocking {
          flow {
            val client = project.createNexusStagingClient(target.baseUrl)
            val description = publication.stagingDescription
            val profiles = client.getProfiles()
            val profile = profiles.firstOrNull { project.group.toString() == it.name }
              ?: profiles.firstOrNull { project.group.toString().startsWith(it.name) }
              ?: profiles.firstOrNull()
              ?: return@flow

            // Check staging repository if exists in nexus
            val existing = client.getRepositories().firstOrNull {
              it.description == description && it.transitioning.not() && it.type == "open"
            }

            target.stagingId = when {
              existing != null -> existing.repositoryId
              else -> client.createRepository(profile.id, description)
            }

            // Redirects the repository url to specific staging repository (release)
            repository.setUrl(target.url)

            emit(Unit)
          }.retry(retries = 5) {
            // Retry after 1000 ms
            delay(1000)
            true
          }.collect()
        }

        println("Publishing to ${repository.url}")
      }
    }

  /**
   * Configures the task used to close and release sonatype staging repository.
   */
  private fun Project.taskCloseAndReleaseSonatypeRepository(target: SonatypeTarget) =
    tasks.create("closeAndReleaseSonatypeRepository") {
      group = PublishingPlugin.PUBLISH_TASK_GROUP
      description = "Closes release version in the nexus repository."
      doLast {
        runBlocking {
          val client = project.createNexusStagingClient(target.baseUrl)
          val repositoryIds = tasks.withType<PublishToMavenRepository>().mapNotNull {
            flow {
              val description = it.publication.stagingDescription
              emit(client.getRepositories()
                .firstOrNull { it.description == description }
                ?.repositoryId)
            }.retry(retries = 5) {
              // Retry after 1000 ms
              delay(1000)
              true
            }.first()
          }
          // Collect the repository ids to close
          repositoriesToClose
            .getOrPut(target.baseUrl) { mutableSetOf() }
            .addAll(repositoryIds)
        }
      }
    }
}