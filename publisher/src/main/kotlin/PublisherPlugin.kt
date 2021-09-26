/*
 * Copyright (c) 2021. The Meowool Organization Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
@file:Suppress("NAME_SHADOWING")

package com.meowool.gradle.toolkit.publisher

import MavenLocalDestination
import SonatypeDestination
import addIfNotExists
import com.gradle.publish.PluginBundleExtension
import com.gradle.publish.PublishPlugin
import com.meowool.gradle.toolkit.publisher.internal.androidExtension
import com.meowool.gradle.toolkit.publisher.internal.buildListener
import com.meowool.gradle.toolkit.publisher.internal.configureAllVariants
import com.meowool.gradle.toolkit.publisher.internal.createNexusStagingClient
import com.meowool.gradle.toolkit.publisher.internal.createSourcesJar
import com.meowool.gradle.toolkit.publisher.internal.isAndroid
import com.meowool.gradle.toolkit.publisher.internal.isCompatible
import com.meowool.gradle.toolkit.publisher.internal.isMultiplatform
import com.meowool.gradle.toolkit.publisher.internal.repositoriesToClose
import com.meowool.gradle.toolkit.publisher.internal.stagingDescription
import com.meowool.sweekt.castOrNull
import com.meowool.sweekt.isNotNull
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

/**
 * A plugin for publishing Gradle or Maven publications.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class PublisherPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    // Apply the plugin earlier, otherwise `kotlin-multiplatform` will lack artifacts
    apply<MavenPublishPlugin>()

    extensions.addIfNotExists("publication") { PublicationExtension(project) }
    afterEvaluate {
      val extension = extensions.getByType<PublicationExtension>()

      // Incompatible project do not enable publishing
      if (isCompatible.not()) {
        if (extension.showIncompatibleWarnings!!) target.logger.warn("Publisher skips incompatible project: '$path'")
        return@afterEvaluate
      }

      apply<DokkaPlugin>()

      if (extension.destinations.isEmpty()) {
        extension.publishToSonatype()
      }

      configureGradlePlugin(extension)
      publishing {
        // The kotlin multiplatform plugin publishes its own components and sources
        if (isMultiplatform.not()) {
          publishComponents(project)
          publishSourcesJar(project)
        }
        configureData(extension)
        configureDokkaJar(extension)
        configureSignings(extension)
        configureRepositories(extension)
      }

      // Sonatype
      initSonatypeBuild()
      extension.destinations.filterIsInstance<SonatypeDestination>().firstOrNull()?.let {
        taskInitializeSonatypeStaging(extension, it)
        taskCloseAndReleaseSonatypeRepository(it)
      }
    }
  }

  private fun Project.configureGradlePlugin(extension: PublicationExtension) = with(extension) {
    // Publish gradle plugin only when `pluginClass` is set
    if (pluginClass != null) {
      apply<PublishPlugin>()
      extensions.configure<PluginBundleExtension> {
        tags = data.tags
        data.url?.let(::setWebsite)
        data.vcs?.let(::setVcsUrl)
      }
      extensions.configure<GradlePluginDevelopmentExtension> {
        plugins.create(data.artifactId) {
          implementationClass = pluginClass
          id = data.pluginId
          displayName = data.displayName
          data.description?.let(::setDescription)
        }
      }
    }
  }

  /**
   * Configures the project components to publish.
   */
  private fun PublishingExtension.publishComponents(project: Project) = with(project) {
    // Configure artifacts with sources jar
    when {
      isAndroid -> androidExtension!!.configureAllVariants { variant ->
        publications.create<MavenPublication>(variant.name) {
          from(components[variant.name])
        }
      }
      else -> publications.create<MavenPublication>("maven") {
        components.findByName("kotlin")?.let(::from) ?: components.findByName("java")?.let(::from)
      }
    }
  }

  /**
   * Configures the project sources jar to publish.
   */
  private fun PublishingExtension.publishSourcesJar(project: Project) = with(project) {
    afterEvaluate {
      val kotlinSourcesJar = tasks.findByName("kotlinSourcesJar")
      val sourcesJar = when {
        isAndroid -> createSourcesJar("androidSourcesJar") {
          from(androidExtension!!.sourceSets.getByName("main").java.srcDirs)
        }
        // Hand it over to kotlin
        kotlinSourcesJar != null -> kotlinSourcesJar
        else -> createSourcesJar("javaSourcesJar") {
          from(extensions.getByType<JavaPluginExtension>().sourceSets.getByName("main").allSource)
        }
      }
      publications.withType<MavenPublication> { artifact(sourcesJar) }
    }
  }

  /**
   * Configures the data of publication.
   */
  private fun PublishingExtension.configureData(extension: PublicationExtension) = with(extension) {
    (destinations + localDestinations).forEach {
      it.project = project
      it.isSnapshot = isSnapshotVersion
    }
    publications.withType<MavenPublication> {
      project.group = data.groupId
      project.version = data.version
      project.description = data.description

      groupId = data.groupId
      version = data.version

      // Do not set the artifactId of plugin, because it is specified by `java-gradle-plugin`:
      // https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_markers
      if (name.endsWith("PluginMarkerMaven").not()) when {
        // Lazy setting the artifact id to avoid replacement by MPP plugin
        project.isMultiplatform -> project.afterEvaluate {
          // The `kotlin-multiplatform` plugin uses the project name as the artifact prefix, so we need to replace it.
          // E.g. library, library-jvm, library-native
          artifactId = data.artifactId + artifactId.removePrefix(project.name)
        }
        else -> artifactId = data.artifactId
      }

      pom {
        name.set(data.displayName)
        url.set(data.url)
        description.set(data.description.toString())

        developers {
          data.developers.forEach {
            developer {
              it.id?.let(id::set)
              it.name?.let(name::set)
              it.email?.let(email::set)
              it.url?.let(url::set)
            }
          }
        }
        licenses {
          data.licenses.forEach {
            license {
              it.name?.let(name::set)
              it.url?.let(url::set)
            }
          }
        }
        organization {
          name.set(data.organizationName)
          name.set(data.organizationUrl)
        }
        scm {
          url.set(data.vcs)
        }

        // Collect all the repositories used in the project to add to the POM
        // See https://maven.apache.org/pom.html#Repositories
        withXml {
          project.repositories
            .filterIsInstance<MavenArtifactRepository>()
            .filterNot { it.url.host.isNotNull() && it.url.host.contains("repo.maven.apache.org") }
            .takeIf { it.isNotEmpty() }
            ?.also { repositories ->
              val xml = this.asNode()
              // Find or create `repositories` node
              val repositoriesNode = xml.children().firstOrNull { it is Node && it.name() == "repositories" } as? Node
                ?: xml.appendNode("repositories")

              repositories.forEach { repo ->
                repositoriesNode.appendNode("repository")?.apply {
                  appendNode("id", repo.name)
                  appendNode("url", repo.url.host)
                }
              }
            }
        }

        data.pomConfigurations.forEach { it() }
      }
    }
  }

  /**
   * Configures the Dokka jar to publish.
   */
  private fun PublishingExtension.configureDokkaJar(extension: PublicationExtension) = with(extension) {
    project.afterEvaluate {
      // We do not publish dokka for the snapshot version to improve the build speed
      if (isSnapshotVersion) return@afterEvaluate

      val dokkaJar by tasks.register<Jar>("dokkaJar") {
        val dokkaTask = tasks.findByName(dokkaFormat!!.taskName).castOrNull()
          ?: tasks.withType<DokkaTask>().first()

        dependsOn(dokkaTask)
        from(dokkaTask.outputDirectory)
        archiveClassifier.set("javadoc")
      }
      // Configures dokka jar to all publications
      publications.withType<MavenPublication> { artifact(dokkaJar) }
    }
  }

  /**
   * Configures the signings to sign artifacts to publish.
   */
  private fun PublishingExtension.configureSignings(extension: PublicationExtension) = with(extension) {
    if (isSignRelease!! || isSignSnapshot!!) {
      project.apply<SigningPlugin>()

      // Signing the artifacts
      project.extensions.configure<SigningExtension> {
        setRequired({
          when {
            isSignRelease!! && isSignSnapshot!!.not() -> isSnapshotVersion.not()
            isSignSnapshot!! && isSignRelease!!.not() -> isSnapshotVersion
            isSignRelease!! && isSignSnapshot!! -> true
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
    when (extension.isLocalVersion) {
      true -> extension.localDestinations
      false -> extension.destinations
    }.forEach {
      val url = it.url
      val maven = if (url == MavenLocalDestination::class) mavenLocal() else maven(it.url)
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
  private fun Project.taskInitializeSonatypeStaging(extension: PublicationExtension, target: SonatypeDestination) =
    tasks.withType<PublishToMavenRepository>().all {
      doFirst {
        // Don't redirect the publishing of snapshot artifacts
        if (target.isSnapshot.not()) runBlocking {
          flow {
            val client = project.createNexusStagingClient(target.baseUrl)
            val description = publication.stagingDescription
            val profiles = client.getProfiles()
            val profile = profiles.firstOrNull { extension.data.groupId == it.name }
              ?: profiles.firstOrNull { extension.data.groupId.startsWith(it.name) }
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
  private fun Project.taskCloseAndReleaseSonatypeRepository(target: SonatypeDestination) =
    tasks.create("closeAndReleaseSonatypeRepository") {
      group = PublishingPlugin.PUBLISH_TASK_GROUP
      description = "Closes release version in the nexus repository."
      doLast {
        runBlocking {
          val client = project.createNexusStagingClient(target.baseUrl)
          val repositoryIds = tasks.withType<PublishToMavenRepository>().mapNotNull {
            flow {
              val description = it.publication.stagingDescription
              emit(
                client.getRepositories()
                  .firstOrNull { it.description == description }
                  ?.repositoryId
              )
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
