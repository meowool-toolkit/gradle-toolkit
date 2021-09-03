@file:Suppress("MemberVisibilityCanBePrivate")

package com.meowool.toolkit.gradle

import DefaultInternalAndroidKey
import GradleToolkitExtension
import OpenSourceLicense
import abiFilters
import android
import com.diffplug.gradle.spotless.SpotlessExtension
import loadKeyProperties
import jitpack
import mavenMirror
import meowoolHomeDir
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.sourceSets
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaTask
import releaseSigning
import sonatype
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText

/**
 * The preset specification configuration of 'Meowool-Organization'.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
open class MeowoolPresetSpec {

  /**
   * The repositories block for the project of this specification.
   */
  open var repositories: RepositoryHandler.(Project) -> Unit = presetRepositories()

  /**
   * Opt-in classes.
   *
   * For more details, see [Opt-in](https://kotlinlang.org/docs/opt-in-requirements.html)
   */
  open val optIn: MutableList<String> = presetOptIn()

  /**
   * The license header of the source codes, if it is `null` and the project is privately, license header will not be
   * written during spotless running.
   *
   * @see spotless
   */
  var licenseHeader: String? = null
    get() = if (field == null && isOpenSourceProject) OpenSourceLicense else null

  /**
   * Whether to currently developing an open source project belonging to 'Meowool Organization'.
   *
   * @see openSourceProject
   * @see privateProject
   */
  var isOpenSourceProject: Boolean = true

  /**
   * Whether to use this specification spotless.
   *
   * @see enableSpotless
   * @see disableSpotless
   */
  open var enabledSpotless: Boolean = true

  /**
   * The configurations list of the project of this specification.
   */
  open val configurations: MutableList<GradleToolkitExtension.() -> Unit> = mutableListOf(
    presetAndroid(),
    presetSpotless(),
    presetPublications(),
  )

  /**
   * Marks that an open source project belonging to the 'Meowool Organization' is currently being developed.
   */
  fun openSourceProject() {
    isOpenSourceProject = true
  }

  /**
   * Marks that a private project is currently being developed.
   */
  fun privateProject() {
    isOpenSourceProject = false
  }

  /**
   * Sets the content in the [file] as the [licenseHeader].
   */
  fun licenseHeader(file: File) {
    licenseHeader = file.readText()
  }

  /**
   * Sets the content in the [path] as the [licenseHeader].
   */
  fun licenseHeader(path: Path) {
    licenseHeader = Files.readString(path)
  }

  /**
   * Uses the [configuration] to configure the [spotless](https://github.com/diffplug/spotless) extension of
   * this specification.
   */
  fun spotless(configuration: SpotlessExtension.(project: Project) -> Unit) {
    configurations += {
      allprojects {
        extensions.findByType<SpotlessExtension>()?.apply { configuration(project) }
      }
    }
  }

  /**
   * Enables the [spotless](https://github.com/diffplug/spotless) plugin.
   */
  fun enableSpotless() {
    enabledSpotless = true
  }

  /**
   * Disables the [spotless](https://github.com/diffplug/spotless) plugin.
   */
  fun disableSpotless() {
    enabledSpotless = false
  }


  /////////////////////////////////////////////////////////////////////////////////////
  ////                                Internal APIs                                ////
  /////////////////////////////////////////////////////////////////////////////////////

  protected fun presetRepositories(): RepositoryHandler.(Project) -> Unit = { project ->
    project.rootProject.rootDir.resolve(".repo").takeIf { it.exists() }?.let(::maven)
    google()
    mavenCentral()
    sonatype()
    mavenMirror(MavenMirrors.Aliyun.JCenter)
    gradlePluginPortal()
    jitpack()
  }

  protected fun presetOptIn() = mutableListOf(
    "kotlin.RequiresOptIn",
    "kotlin.Experimental",
    "kotlin.ExperimentalStdlibApi",
    "kotlin.ExperimentalUnsignedTypes",
    "kotlin.ExperimentalPathApi",
    "kotlin.time.ExperimentalTime",
    "kotlin.contracts.ExperimentalContracts",
    "kotlin.experimental.ExperimentalTypeInference",
    "kotlinx.coroutines.ExperimentalCoroutinesApi",
  )

  protected fun presetSpotless(): GradleToolkitExtension.() -> Unit = {
    allprojects {
      extensions.findByType<SpotlessExtension>()?.apply {
        if (extensions.findByType<JavaPluginExtension>() == null) return@apply
        java {
          // apply a specific flavor of google-java-format
          googleJavaFormat().aosp()
          targetExclude("${buildDir.absolutePath}/**", "**/resources/**")
          endWithNewline()
          trimTrailingWhitespace()
          licenseHeader?.let { licenseHeader(it, "(package |import |public |private)") }
        }
        kotlin {
          targetExclude("${buildDir.absolutePath}/**", "**/resources/**")
          ktlint("0.42.1").userData(
            mapOf(
              "indent_size" to "2",
              "no-unused-imports" to "true",
            )
          )
          endWithNewline()
          trimTrailingWhitespace()
          licenseHeader?.let { licenseHeader(it, "(package |import |@ |class |fun |val |public |private |internal)") }
        }
      }
    }
  }

  protected fun presetPublications(): GradleToolkitExtension.() -> Unit = {
    allprojects {
      project.extensions.findByType<PublicationExtension>()?.apply {
        showIncompatibleWarnings = false
        data {
          license {
            name = "The Apache Software License, Version 2.0"
            url = "https://github.com/meowool/license/blob/main/LICENSE"
          }
          organizationName = "Meowool"
          organizationUrl = "https://github.com/meowool/"
        }
      }
      project.tasks.withType<DokkaTask> {
        dokkaSourceSets.configureEach {
          skipDeprecated.set(true)
          skipEmptyPackages.set(false)
        }
      }

      // Keep spotless before publish.
      project.tasks.findByName("spotlessApply")?.also { spotless ->
        project.tasks.findByName("publish")?.dependsOn(spotless)
      }
    }
  }


  protected fun presetAndroid(): GradleToolkitExtension.() -> Unit = {
    registerLogic {
      android(DefaultInternalAndroidKey) { project ->
        with(project) {
          releaseSigning {
            if (isOpenSourceProject) {
              meowoolHomeDir?.resolve(".key/key.properties")
                ?.takeIf { it.exists() }
                ?.let(::loadKeyProperties)
                ?: println(
                  "There is a key of signing common to open source projects in 'Meowool-Organization', " +
                    "for normalization, it should be used."
                )
            } else {
              meowoolHomeDir?.resolve(".key/key-internal.properties")
                ?.takeIf { it.exists() }
                ?.let(::loadKeyProperties)
            }
          }

          abiFilters(NdkAbi.Armeabi_v7a, NdkAbi.Arm64_v8a)
        }
      }
    }
  }
}