/*
 * Copyright (c) $\YEAR. The Meowool Organization Open Source Project
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
import org.gradle.kotlin.dsl.apply

internal fun RootGradleDslExtension.presetRepositories(loadSnapshots: Boolean = false) {
  rootProject.rootDir.resolve(".repo").takeIf { it.exists() }?.let(::addMaven)
  addGoogle()
  addMavenCentral()
  addJitpack()
  addMavenMirror(MavenMirrors.Aliyun.JCenter)
  addGradlePluginPortal()
  addSonatype()
  if (loadSnapshots) addSonatypeSnapshots()
}

internal fun RootGradleDslExtension.presetKotlinCompilerArgs() =
  rootProject.useExperimentalAnnotations(
    "kotlin.RequiresOptIn",
    "kotlin.time.ExperimentalTime",
    "kotlin.Experimental",
    "kotlin.ExperimentalStdlibApi",
    "kotlin.ExperimentalUnsignedTypes",
    "kotlin.contracts.ExperimentalContracts",
    "kotlin.experimental.ExperimentalTypeInference",
    "kotlinx.coroutines.ExperimentalCoroutinesApi"
  )

internal fun RootGradleDslExtension.presetSpotless(
  isOpenSourceProject: Boolean
) {
  project.allprojects {
    if (!buildFile.exists()) return@allprojects

    apply<SpotlessPlugin>()

    extensions.configure<SpotlessExtension>("spotless") {
      kotlin {
        targetExclude("${buildDir.absolutePath}/**", "**/resources/**")
        ktlint("0.41.0").userData(
          mapOf(
            "indent_size" to "2",
            "no-unused-imports" to "true",
            "disabled_rules" to "no-wildcard-imports"
          )
        )
        endWithNewline()
        trimTrailingWhitespace()
        if (isOpenSourceProject) licenseHeader(OpenSourceLicense, "(package |import |@file)")
      }
    }
  }
}

internal fun RootGradleDslExtension.presetPublishing() {
//  // TODO Support
//  // https://github.com/vanniktech/gradle-maven-publish-plugin#setting-properties
//  data += PublishInfo(
//    developerId = "meowool",
//    developerName = "Meowool Organization",
//    developerUrl = "https://github.com/meowool/",
//    licenceName = "The Apache Software License, Version 2.0",
//    licenceUrl = "https://github.com/meowool/license/blob/main/LICENSE"
//  )

  project.publishSubprojects()
  project.allprojects {
    if (!buildFile.exists()) return@allprojects

    mavenPublish()

    // Keep spotless before publish.
    afterEvaluate {
      val spotlessApply = tasks.findByName("spotlessApply") ?: return@afterEvaluate
      tasks.findByName("publish")?.dependsOn(spotlessApply)
    }
  }
}

internal fun RootGradleDslExtension.presetAndroid(
  isOpenSourceProject: Boolean
) = shareAndroid { project ->
  with(project) {
    releaseSigning {
      if (isOpenSourceProject) {
        meowoolHomeDir.resolve(".key/key.properties")
          .takeIf { it.exists() }
          ?.let(::loadKeyProperties)
          ?: println(
            "There is a key common to open source projects in 'Meowool-Organization', " +
              "for normalization, it should be used."
          )
      } else {
        meowoolHomeDir.resolve(".key/key-internal.properties")
          .takeIf { it.exists() }
          ?.let(::loadKeyProperties)
      }
    }

    abiFilters(NdkAbi.Armeabi_v7a, NdkAbi.Arm64_v8a)
  }
}
