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
 *
 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
@file:Suppress("MemberVisibilityCanBePrivate", "SpellCheckingInspection")

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
import getNamedOrNull
import javaWhenAvailable
import jitpack
import kotlinWhenAvailable
import loadKeyProperties
import mavenMirror
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import meowoolHomeDir
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.dsl.RepositoryHandler
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
import kotlin.reflect.KClass

/**
 * The preset specification configuration of 'Meowool-Organization'.
 *
 * @author 凛 (RinOrz)
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
  open val optIn: MutableSet<String> = presetOptIn()

  /**
   * Kotlin compiler arguments.
   *
   * For more details, see [Kotlin Source](https://github.com/JetBrains/kotlin/blob/master/compiler/cli/cli-common/src/org/jetbrains/kotlin/cli/common/arguments/CommonCompilerArguments.kt)
   */
  open val compilerArguments: MutableSet<String> = presetCompilerArguments()

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
   * The project whether to use this specification spotless.
   *
   * If the [Project.getProjectDir] contains a file named `.skip-spotless`, the project will not enable spotless
   * plugin by 'Meowool-Spec' anyway.
   *
   * @see enableSpotless
   * @see disableSpotless
   */
  open var enabledSpotless: (Project) -> Boolean = { true }

  /**
   * The project whether to use this specification of metalava.
   *
   * If the [Project.getProjectDir] contains a file named `.skip-metalava`, the project will not enable metalava
   * plugin by 'Meowool-Spec' anyway.
   *
   * @see enableMetalava
   * @see disableMetalava
   */
  open var enabledMetalava: (Project) -> Boolean = { false }

  /**
   * The project whether to use this specification of binary-compatibility-validator.
   *
   * If the [Project.getProjectDir] contains a file named `.skip-bcv`, the project will not enable
   * binary-compatibility-validator plugin by 'Meowool-Spec' anyway.
   *
   * @see enableBinaryCompatibilityValidator
   * @see disableBinaryCompatibilityValidator
   */
  open var enabledBinaryCompatibilityValidator: (Project) -> Boolean = { true }

  /**
   * The project whether to use this specification of publisher.
   *
   * If the [Project.getProjectDir] contains a file named `.skip-publisher`, the project will not enable publisher
   * plugin by 'Meowool-Spec' anyway.
   *
   * @see enablePublisher
   * @see disablePublisher
   */
  open var enabledPublisher: (Project) -> Boolean = { true }

  /**
   * The configurations list of the project of this specification.
   */
  open val configurations: MutableList<GradleToolkitExtension.() -> Unit> = mutableListOf(
    presetAndroid(),
    presetSpotless(),
    presetPublications(),
    presetBinaryCompatibilityValidator(),
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
   * Enables the [spotless](https://github.com/diffplug/spotless) plugin.
   *
   * If the [Project.getProjectDir] contains a file named `.skip-spotless`, the project will not enable spotless
   * plugin by 'Meowool-Spec' anyway.
   *
   * @param filterProject Projects whose predicate is `true` will enable the spotless plugin.
   */
  fun enableSpotless(filterProject: (Project) -> Boolean = { true }) {
    enabledSpotless = filterProject
  }

  /**
   * Disables the [spotless](https://github.com/diffplug/spotless) plugin.
   *
   * @param filterProject Projects whose predicate is `true` will disable the spotless plugin.
   */
  fun disableSpotless(filterProject: (Project) -> Boolean = { true }) {
    enabledSpotless = { filterProject(it).not() }
  }

  /**
   * Enables the [metalava](https://github.com/tylerbwong/metalava-gradle) plugin.
   *
   * If the [Project.getProjectDir] contains a file named `.skip-metalava`, the project will not enable metalava
   * plugin by 'Meowool-Spec' anyway.
   *
   * @param filterProject Projects whose predicate is `true` will enable the metalava plugin.
   */
  fun enableMetalava(filterProject: (Project) -> Boolean = { true }) {
    enabledMetalava = filterProject
  }

  /**
   * Disables the [metalava](https://github.com/tylerbwong/metalava-gradle) plugin.
   *
   * @param filterProject Projects whose predicate is `true` will disable the metalava plugin.
   */
  fun disableMetalava(filterProject: (Project) -> Boolean = { true }) {
    enabledMetalava = { filterProject(it).not() }
  }

  /**
   * Enables the [binary-compatibility-validator](https://github.com/Kotlin/binary-compatibility-validator) plugin.
   *
   * If the [Project.getProjectDir] contains a file named `.skip-metalava`, the project will not enable metalava
   * plugin by 'Meowool-Spec' anyway.
   *
   * @param filterProject Projects whose predicate is `true` will enable the BCV plugin.
   */
  fun enableBinaryCompatibilityValidator(filterProject: (Project) -> Boolean = { true }) {
    enabledBinaryCompatibilityValidator = filterProject
  }

  /**
   * Disables the [binary-compatibility-validator](https://github.com/Kotlin/binary-compatibility-validator) plugin.
   *
   * @param filterProject Projects whose predicate is `true` will disable the BCV plugin.
   */
  fun disableBinaryCompatibilityValidator(filterProject: (Project) -> Boolean = { true }) {
    enabledBinaryCompatibilityValidator = { filterProject(it).not() }
  }

  /**
   * Enables the [publisher](https://github.com/meowool-toolkit/gradle-toolkit/publisher) plugin.
   *
   * If the [Project.getProjectDir] contains a file named `.skip-publisher`, the project will not enable publisher
   * plugin by 'Meowool-Spec' anyway.
   *
   * @param filterProject Projects whose predicate is `true` will enable the publisher plugin.
   */
  fun enablePublisher(filterProject: (Project) -> Boolean = { true }) {
    enabledPublisher = filterProject
  }

  /**
   * Disables the [publisher](https://github.com/meowool-toolkit/gradle-toolkit/publisher) plugin.
   *
   * @param filterProject Projects whose predicate is `true` will disable the publisher plugin.
   */
  fun disablePublisher(filterProject: (Project) -> Boolean = { true }) {
    enabledPublisher = { filterProject(it).not() }
  }

  // ///////////////////////////////////////////////////////////////////////////////////
  // //                                Internal APIs                                ////
  // ///////////////////////////////////////////////////////////////////////////////////

  protected fun presetRepositories(): RepositoryHandler.(Project) -> Unit = { project ->
    project.rootDir.resolve(".repo").takeIf(File::exists)?.let(::maven)
    project.projectDir.resolve(".repo").takeIf(File::exists)?.let(::maven)
    google()
    mavenCentral()
    sonatype()
    mavenMirror(MavenMirrors.Aliyun.JCenter)
    gradlePluginPortal()
    jitpack()
  }

  /**
   * [See](https://github.com/JetBrains/kotlin/blob/master/compiler/cli/cli-common/src/org/jetbrains/kotlin/cli/common/arguments/CommonCompilerArguments.kt)
   */
  protected fun presetCompilerArguments() = mutableSetOf(
    "-Xnew-inference",
    "-Xno-check-actual",
    "-Xskip-prerelease-check",
    "-Xsuppress-version-warnings",
    "-Xenable-builder-inference",
    "-Xskip-metadata-version-check",
    // See: https://kotlinlang.org/docs/whatsnew1530.html#improvements-to-type-inference-for-recursive-generic-types
    "-Xself-upper-bound-inference",
    // See: https://kotlinlang.org/docs/whatsnew1530.html#eliminating-builder-inference-restrictions
    "-Xunrestricted-builder-inference",
  )

  protected fun presetOptIn() = mutableSetOf(
    "kotlin.RequiresOptIn",
    "kotlin.Experimental",
    "kotlin.ExperimentalStdlibApi",
    "kotlin.ExperimentalUnsignedTypes",
    "kotlin.ExperimentalPathApi",
    "kotlin.time.ExperimentalTime",
    "kotlin.contracts.ExperimentalContracts",
    "kotlinx.coroutines.FlowPreview",
    "kotlinx.coroutines.ExperimentalCoroutinesApi",
    "kotlin.experimental.ExperimentalTypeInference",
    "kotlinx.serialization.ExperimentalSerializationApi",
  )

  protected fun presetSpotless(): GradleToolkitExtension.() -> Unit = {
    allprojects {
      extensions.findByType<SpotlessExtension>()?.apply {
        fun ktlintData() = mapOf(
          "disabled_rules" to "filename",
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
          licenseHeader?.let {
            licenseHeader(
              it,
              "(import |project.|rootProject.|pluginManagement|plugins|buildscript|tasks|apply|rootProject|android|@)"
            )
          }
        }

        fun sources(suffix: String): List<File> = sourceSets.flatMap { set ->
          set.allSource.sourceDirectories.asFileTree.filter {
            it.startsWith(project.buildDir).not() && it.extension == suffix
          }
        }

        javaWhenAvailable(project) {
          target(sources("java"))
          // apply a specific flavor of google-java-format
          googleJavaFormat().aosp()
          endWithNewline()
          indentWithSpaces()
          trimTrailingWhitespace()
          licenseHeader?.let { licenseHeader(it, "(package |import |public |private )") }
        }
        kotlinWhenAvailable(project) {
          target(sources("kt"))
          ktlint().userData(ktlintData())
          endWithNewline()
          indentWithSpaces()
          trimTrailingWhitespace()
          licenseHeader?.let {
            licenseHeader(it, "(package |import |class |fun |val |public |private |internal |@)")
          }
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

      project.tasks.withType<DokkaTask>().configureEach {
        dokkaSourceSets.configureEach {
          skipDeprecated.set(true)
          skipEmptyPackages.set(false)
        }
      }

      // Keep spotless before publish
      setPublishTaskDependsOn(SpotlessApply::class)
    }
  }

  protected fun presetMetalava(): GradleToolkitExtension.() -> Unit = {
    allprojects {
      project.extensions.findByType<MetalavaExtension>()?.apply {
        ignoreUnsupportedModules = true
        includeSignatureVersion = false
      }

      // Output meta api before publish
      setPublishTaskDependsOn("metalavaGenerateSignature")
    }
  }

  protected fun presetBinaryCompatibilityValidator(): GradleToolkitExtension.() -> Unit = {
    allprojects {
      // Output binary api
      setPublishTaskDependsOn("apiDump")
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
                ?: project.logger.warn(
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

  private fun Project.setPublishTaskDependsOn(baseName: String) {
    var base: Task? = null
    var publish: Task? = null
    project.tasks.configureEach {
      if (base == null) base = project.tasks.getNamedOrNull(baseName)
      if (publish == null) publish = project.tasks.getNamedOrNull("publish")
      when (name) {
        baseName -> {
          base = this
          publish?.dependsOn(this)
        }
        "publish" -> {
          publish = this
          base?.let { this.dependsOn(it) }
        }
      }
    }
  }

  private fun Project.setPublishTaskDependsOn(baseType: KClass<out Task>) {
    var base: Task? = null
    var publish: Task? = null
    project.tasks.configureEach {
      if (base == null) base = project.tasks.withType(baseType).firstOrNull()
      if (publish == null) publish = project.tasks.getNamedOrNull("publish")
      when {
        baseType.isInstance(this) -> {
          base = this
          publish?.dependsOn(this)
        }
        name == "publish" -> {
          publish = this
          base?.let { this.dependsOn(it) }
        }
      }
    }
  }
}
