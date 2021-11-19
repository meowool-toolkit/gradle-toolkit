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
@file:Suppress("SpellCheckingInspection")

package com.meowool.gradle.toolkit

import addFreeCompilerArgs
import addIfNotExists
import com.meowool.gradle.toolkit.internal.GradleToolkitExtensionImpl
import com.meowool.gradle.toolkit.internal.GradleToolkitExtensionImpl.Companion.toolkitExtensionImpl
import kotlinJvmOptions
import kotlinMultiplatformExtensionOrNull
import kotlinOptions
import optIn
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Enhanced plugin core for gradle kotlin dsl.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class GradleToolkitCorePlugin : Plugin<Any> {
  override fun apply(target: Any) {
    when (target) {
      is Settings -> target.gradle.rootProject { bootstrap() }
      is Project -> target.bootstrap()
    }
  }

  private fun Project.bootstrap() {
    require(this == rootProject) { "'gradle-toolkit' can only be applied to the root project." }
    // Register extension
    rootProject.extensions.addIfNotExists<GradleToolkitExtension>("gradleToolkit") {
      GradleToolkitExtensionImpl(rootProject)
    }
    allprojects {
      val extension = toolkitExtensionImpl

      setJvmTarget(extension)

      // See: https://github.com/JetBrains/kotlin/blob/1.6.0/libraries/stdlib/jvm/src/kotlin/jvm/JvmDefault.kt#L37-L50
      kotlinOptions { addFreeCompilerArgs("-Xjvm-default=all") }

      afterEvaluate {
        optIn("kotlin.RequiresOptIn")

        // TODO: Remove when https://github.com/gradle/gradle/issues/18935 fixed
        afterEvaluate { setJvmTarget(extension) }; setJvmTarget(extension)

        kotlinMultiplatformExtensionOrNull?.apply {
          // TODO Custom shared source sets until https://youtrack.jetbrains.com/issue/KT-42466 is completed
          sourceSets.all {
            when (name) {
              "jvmMain", "androidMain" -> kotlin.srcDirs("src/jvmShareMain/kotlin")
              "jvmTest", "androidTest" -> kotlin.srcDirs("src/jvmShareTest/kotlin")
            }
          }

          targets.all {
            if (this is KotlinAndroidTarget) {
              // Overwrite can be avoided in this case
              if (publishLibraryVariants.isNullOrEmpty()) publishAllLibraryVariants()
            }
          }
        } ?: extensions.findByType<SourceSetContainer>()?.apply {
          findByName("main")?.java?.srcDirs("src/main/kotlin")
          findByName("test")?.java?.srcDirs("src/test/kotlin")
        }
      }
    }
  }

  private fun Project.setJvmTarget(extension: GradleToolkitExtension) {
    extensions.findByType<JavaPluginExtension>()?.apply {
      sourceCompatibility = extension.defaultJvmTarget
      targetCompatibility = extension.defaultJvmTarget
    }

    tasks.withType<JavaCompile> {
      sourceCompatibility = extension.defaultJvmTarget.toString()
      targetCompatibility = extension.defaultJvmTarget.toString()
    }

    tasks.withType<KotlinCompile> {
      sourceCompatibility = extension.defaultJvmTarget.toString()
      targetCompatibility = extension.defaultJvmTarget.toString()
    }

    kotlinJvmOptions {
      jvmTarget = extension.defaultJvmTarget.toString()
    }
  }
}
