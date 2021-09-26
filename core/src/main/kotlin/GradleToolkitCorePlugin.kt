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
package com.meowool.gradle.toolkit

import addIfNotExists
import com.meowool.gradle.toolkit.internal.DefaultJavaVersion
import com.meowool.gradle.toolkit.internal.GradleToolkitExtensionImpl
import kotlinJvmOptions
import kotlinMultiplatformExtensionOrNull
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
      kotlinJvmOptions {
        @Suppress("DEPRECATION")
        useIR = true
      }

      extensions.findByType<JavaPluginExtension>()?.apply {
        sourceCompatibility = DefaultJavaVersion
        targetCompatibility = DefaultJavaVersion
      }

      tasks.withType<JavaCompile> {
        sourceCompatibility = DefaultJavaVersion.toString()
        targetCompatibility = DefaultJavaVersion.toString()
      }

      tasks.withType<KotlinCompile> {
        sourceCompatibility = DefaultJavaVersion.toString()
        targetCompatibility = DefaultJavaVersion.toString()
      }

      afterEvaluate {
        optIn("kotlin.RequiresOptIn")

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
}
