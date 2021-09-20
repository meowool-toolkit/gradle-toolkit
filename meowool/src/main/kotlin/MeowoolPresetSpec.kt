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
@file:Suppress("MemberVisibilityCanBePrivate")

package com.meowool.gradle.toolkit.internal

import MavenMirrors
import NdkAbi
import abiFilters
import android
import com.diffplug.gradle.spotless.SpotlessApply
import com.diffplug.gradle.spotless.SpotlessExtension
import com.meowool.gradle.toolkit.GradleToolkitExtension
import com.meowool.gradle.toolkit.android.internal.AndroidLogicRegistry.DefaultCandidateAndroidKey
import com.meowool.gradle.toolkit.publisher.PublicationExtension
import jitpack
import loadKeyProperties
import mavenMirror
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import meowoolHomeDir
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
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

/**
 * The preset specification configuration of 'Meowool-Organization'.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
open class MeowoolPresetSpec internal constructor() {

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
   * written during spotless running, otherwise use default license header by Meowool organization.
   *
   * @see spotless
   */
  var licenseHeader: String? = null
    get() = if (field == null && isOpenSourceProject) defaultOpenSourceLicense(licenseUrl!!) else null

  /**
   * The license url of the license header, if it is `null`, license url will not be
   * written during spotless running, otherwise use default license url by Apache 2.0.
   *
   * @see spotless
   */
  var licenseUrl: String? = null
    get() = if (field == null) Apache2License else null

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
   * Whether to use this specification of metalava.
   *
   * @see enableMetalava
   * @see disableMetalava
   */
  open var enabledMetalava: Boolean = true

  /**
   * Whether to use this specification of publisher.
   *
   * @see enablePublisher
   * @see disablePublisher
   */
  open var enabledPublisher: Boolean = true

  /**
   * The configurations list of the project of this specification.
   */
  open val configurations: MutableList<GradleToolkitExtension.() -> Unit> = mutableListOf(
    presetAndroid(),
    presetSpotless(),
    presetPublications(),
    presetMetalava(),
  )

  /**
   * Marks that an open source project belonging to the 'Meowool Organization' is currently being developed.
   */
  fun openSourceProject(licenseUrl: String) {
    isOpenSourceProject = true
    this.licenseUrl = licenseUrl
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
   * Enables the [publisher](https://github.com/meowool-toolkit/gradle-toolkit/publisher) plugin.
   */
  fun enablePublisher() {
    enabledPublisher = true
  }

  /**
   * Disables the [publisher](https://github.com/meowool-toolkit/gradle-toolkit/publisher) plugin.
   */
  fun disablePublisher() {
    enabledPublisher = false
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

  /**
   * Enables the [metalava](https://github.com/tylerbwong/metalava-gradle) plugin.
   */
  fun enableMetalava() {
    enabledMetalava = true
  }

  /**
   * Disables the [metalava](https://github.com/tylerbwong/metalava-gradle) plugin.
   */
  fun disableMetalava() {
    enabledMetalava = false
  }

  // ///////////////////////////////////////////////////////////////////////////////////
  // //                                Internal APIs                                ////
  // ///////////////////////////////////////////////////////////////////////////////////

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
    allprojects(afterEvaluate = false) {
      extensions.findByType<SpotlessExtension>()?.apply {
        fun ktlintData() = mapOf(
          "indent_size" to "2",
          "chain-wrapping" to "true",
          "modifier-order" to "true",
          "string-template" to "true",
          "no-semi" to "true",
          "no-unit-return" to "true",
          "no-unused-imports" to "true",
          "no-trailing-spaces" to "true",
          "no-wildcard-imports" to "true",
          "no-line-break-before-assignment" to "true",
          "experimental:multiline-if-else" to "true",
          "experimental:double-colon-spacing" to "true",
          "experimental:spacing-between-declarations-with-comments" to "true",
        )

        kotlinGradle {
          ktlint().userData(ktlintData())
          endWithNewline()
          trimTrailingWhitespace()
          licenseHeader?.let { licenseHeader(it, "(import |plugins|buildscript|tasks|apply|rootProject|@)") }
        }

        afterEvaluate {
          if (extensions.findByType<JavaPluginExtension>() != null) {
            fun sources(suffix: String): List<File> = sourceSets.flatMap { set ->
              set.allSource.sourceDirectories.asFileTree.filter { it.extension == suffix }
            }
            java {
              target(sources("java"))
              // apply a specific flavor of google-java-format
              googleJavaFormat().aosp()
              endWithNewline()
              trimTrailingWhitespace()
              licenseHeader?.let { licenseHeader(it, "(package |import |public |private )") }
            }
            kotlin {
              target(sources("kt"))
              ktlint().userData(ktlintData())
              endWithNewline()
              trimTrailingWhitespace()
              licenseHeader?.let { licenseHeader(it, "(package |import |class |fun |val |public |private |internal |@)") }
            }
          }
        }
      }
    }
  }

  protected fun presetPublications(): GradleToolkitExtension.() -> Unit = {
    allprojects(afterEvaluate = false) {
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

      // Keep spotless before publish
      project.tasks.withType<SpotlessApply> {
        afterEvaluate {
          project.tasks.findByName("publish")?.dependsOn(this@withType)
        }
      }
    }
  }

  protected fun presetMetalava(): GradleToolkitExtension.() -> Unit = {
    allprojects(afterEvaluate = false) {
      project.extensions.findByType<MetalavaExtension>()?.apply {
        ignoreUnsupportedModules = true
        includeSignatureVersion = false
      }

      // Output meta api before publish
      project.tasks.all {
        if (name == "metalavaGenerateSignature") {
          afterEvaluate {
            project.tasks.findByName("publish")?.dependsOn(this@all)
          }
        }
      }
    }
  }

  protected fun presetAndroid(): GradleToolkitExtension.() -> Unit = {
    registerLogic {
      android(DefaultCandidateAndroidKey) { project ->
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
