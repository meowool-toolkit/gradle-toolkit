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

internal fun RootGradleDslExtension.presetSpotless(isOpenSourceProject: Boolean) = allprojects {
  afterEvaluate {
    if (this.isRegular.not()) return@afterEvaluate

    apply<SpotlessPlugin>()

    extensions.configure<SpotlessExtension>("spotless") {
      java {
        targetExclude("${buildDir.absolutePath}/**", "**/resources/**")
        endWithNewline()
        trimTrailingWhitespace()
        if (isOpenSourceProject) licenseHeader(OpenSourceLicense, "(package |import |@file)")
      }
      kotlin {
        targetExclude("${buildDir.absolutePath}/**", "**/resources/**")
        ktlint("0.41.0").userData(
          mapOf(
            "indent_size" to "2",
            "no-unused-imports" to "true",
          )
        )
        endWithNewline()
        trimTrailingWhitespace()
        if (isOpenSourceProject) licenseHeader(OpenSourceLicense, "(package |import |@file)")
      }
    }
  }
}

internal fun RootGradleDslExtension.presetPublishing(
  enabledPublish: Boolean,
  publishRootProject: Boolean,
  publishRepo: Array<RepoUrl>,
  publishPom: PublishPom,
) = allprojects {
  afterEvaluate {
    if (this.isRegular.not()) return@afterEvaluate
    if (!publishRootProject && this == rootProject) return@afterEvaluate
    meowoolMavenPublish(publishRepo, publishPom, enabledPublish = enabledPublish)
  }
}

internal fun RootGradleDslExtension.presetAndroid(
  isOpenSourceProject: Boolean,
) = shareAndroid { project ->
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
      plugins.hasPlugin("java")
    )
