@file:Suppress("NOTHING_TO_INLINE")

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.maybeCreate
import org.gradle.kotlin.dsl.publishing
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Configure the maven publishing.
 *
 * @param repo The urls of the target repository to publish to.
 * @param pom The maven pom data of artifact to published.
 * @param releaseSigning Whether to signing the artifact of release version.
 * @param snapshotSigning Whether to signing the artifact of snapshot version.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.mavenPublish(
  repo: Array<RepoUrl> = arrayOf(LocalRepo, SonatypeRepo()),
  pom: PublishPom = publishPom(),
  releaseSigning: Boolean = true,
  snapshotSigning: Boolean = false,
) = afterEvaluate {
  if (!plugins.hasPlugin(MavenPublishPlugin::class)) apply<MavenPublishPlugin>()
  if (!plugins.hasPlugin(DokkaPlugin::class)) apply<DokkaPlugin>()
  if (!plugins.hasPlugin(SigningPlugin::class) && (releaseSigning || snapshotSigning)) apply<SigningPlugin>()

  val androidExtension = extensions.findByName("android") as? BaseExtension
  val isAndroid = androidExtension != null

  val dokkaJar by tasks.register<Jar>("dokkaJar") {
    archiveClassifier.set("javadoc")
    tasks.withType<DokkaTask>().toTypedArray().also { tasks ->
      dependsOn(tasks)
      from(tasks.map { it.outputDirectory })
    }
  }

  val sourcesJar by tasks.register<Jar>(if (isAndroid) "androidSourcesJar" else "sourcesJar") {
    archiveClassifier.set("sources")
    val kotlinSourcesJar = tasks.findByName("kotlinSourcesJar")
    when {
      isAndroid -> from(androidExtension!!.sourceSets.getByName("main").java.srcDirs)
      // Hand it over to kotlin
      kotlinSourcesJar != null -> dependsOn(kotlinSourcesJar)
      else -> from(convention.getPlugin(JavaPluginConvention::class.java).sourceSets.getByName("main").allSource)
    }
  }

  publishing {
    when {
      isAndroid -> androidExtension?.variants {
        publications.create<MavenPublication>(this.name) {
          from(components.getByName(this.name))
          artifact(sourcesJar)
        }
      }
      else -> publications.create<MavenPublication>("maven") {
        from(components.getByName("java"))
        artifact(sourcesJar)
      }
    }

    publications.withType<MavenPublication> {
      artifact(dokkaJar)
      pom { pom.configuration(this) }
      version = pom.version
      groupId = pom.group
      // There will be a suffix when a multi-platform project, so use the way of prefix replacing.
      artifactId = pom.artifact + artifactId.removePrefix(project.name)
    }

    // Target repository to publish to.
    repositories {
      repo.forEach {
        val url = if (pom.isSnapshot) it.snapshots else it.releases
        val maven = if (url == LocalRepo::class) mavenLocal() else maven(url)
        if (it.requiredCertificate) maven.credentials(PasswordCredentials::class.java)
      }
    }
  }

  // Signing the artifact.
  extensions.configure<SigningExtension> {
    val isSigning = when {
      releaseSigning && !snapshotSigning -> pom.isSnapshot.not()
      !releaseSigning && snapshotSigning -> pom.isSnapshot
      releaseSigning && snapshotSigning -> true
      else -> false
    }
    setRequired { isSigning && gradle.taskGraph.hasTask("uploadArchives") }
    if (isSigning) sign(publishing.publications)
  }
}

/**
 * Configure the maven publishing.
 *
 * @param repo The url of the target repository to publish to.
 * @param pom The maven pom data of artifact to published.
 * @param releaseSigning Whether to signing the artifact of release version.
 * @param snapshotSigning Whether to signing the artifact of snapshot version.
 */
inline fun Project.mavenPublish(
  repo: RepoUrl,
  pom: PublishPom = publishPom(),
  releaseSigning: Boolean = true,
  snapshotSigning: Boolean = false,
) = mavenPublish(arrayOf(repo), pom, releaseSigning, snapshotSigning)


private fun BaseExtension.variants(configuration: BaseVariant.() -> Unit) {
  (this as? AppExtension)?.applicationVariants?.configureEach(configuration)
    ?: (this as? LibraryExtension)?.libraryVariants?.configureEach(configuration)
    ?: (this as? TestedExtension)?.testVariants?.configureEach(configuration)
}

///**
// * Add a task to publish all subprojects.
// */
//fun Project.publishSubprojects() {
//  rootProject.allprojects {
//    tasks.register("publishSubprojects") {
//      val publishTasks = subprojects
//        .mapNotNull { it.tasks.findByName("publish") }
//        .toTypedArray()
//      dependsOn(*publishTasks)
//      mustRunAfter(*publishTasks)
//      group = "publishing"
//    }
//  }
//}
