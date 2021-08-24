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
 */
@file:Suppress("SpellCheckingInspection")

import com.android.build.gradle.AppExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import extension.RootGradleDslExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findPlugin

internal fun RootGradleDslExtension.presetRepositories(loadSnapshots: Boolean = false) {
  rootProject.rootDir.resolve(".repo").takeIf { it.exists() }?.let(::repoMaven)
  repoGoogle()
  repoMavenCentral()
  repoJitpack()
  repoMavenMirror(MavenMirrors.Aliyun.JCenter)
  repoGradlePluginPortal()
  repoSonatype()
  if (loadSnapshots) repoSonatypeSnapshots()
}

internal fun RootGradleDslExtension.presetKotlinCompilerArgs() = allprojects {
  afterEvaluate {
    useExperimentalAnnotations(
      "kotlin.RequiresOptIn",
      "kotlin.Experimental",
      "kotlin.ExperimentalStdlibApi",
      "kotlin.ExperimentalUnsignedTypes",
      "kotlin.time.ExperimentalTime",
      "kotlin.contracts.ExperimentalContracts",
      "kotlinx.coroutines.ExperimentalCoroutinesApi",
      "kotlin.experimental.ExperimentalTypeInference",
    )
  }
}

internal fun RootGradleDslExtension.presetSpotless(
  licenseHeader: String?,
  configuration: SpotlessExtension.() -> Unit = {}
) = allprojects {
  afterEvaluate {
    if (this.isRegular.not()) return@afterEvaluate

    apply<SpotlessPlugin>()

    extensions.configure<SpotlessExtension>("spotless") {
      java {
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
      configuration()
    }
  }
}

internal fun RootGradleDslExtension.presetPublishing(
  enabledPublish: Boolean,
  publishRootProject: Boolean,
  publishAndroidAppProject: Boolean,
  publishReleaseSigning: Boolean,
  publishSnapshotSigning: Boolean,
  publishRepo: Array<RepoUrl>,
  publishPom: Project.() -> PublishPom,
) = allprojects {
  afterEvaluate {
    if (this.isRegular.not()) return@afterEvaluate
    if (!publishRootProject && this == rootProject) return@afterEvaluate
    if (!publishAndroidAppProject && extensions.findByName("android") is AppExtension) return@afterEvaluate

    meowoolMavenPublish(
      repo = publishRepo,
      pom = publishPom(),
      enabledPublish = enabledPublish,
      releaseSigning = publishReleaseSigning,
      snapshotSigning = publishSnapshotSigning
    )
  }
}

internal fun RootGradleDslExtension.presetAndroid(isOpenSourceProject: Boolean) = shareAndroid { project ->
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

private val Project.isRegular: Boolean
  get() = buildFile.exists() && (
    convention.findPlugin<JavaPluginConvention>() != null ||
      extensions.findByName("kotlin") != null ||
      extensions.findByName("android") != null ||
      plugins.hasPlugin("kotlin") ||
      plugins.hasPlugin("org.gradle.kotlin.kotlin-dsl") ||
      plugins.hasPlugin("org.gradle.kotlin.kotlin-dsl.base") ||
      plugins.hasPlugin("java-gradle-plugin") ||
      plugins.hasPlugin("java-library") ||
      plugins.hasPlugin("java"))
