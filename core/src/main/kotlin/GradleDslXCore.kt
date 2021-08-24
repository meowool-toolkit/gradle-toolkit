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

import extension.GradleDslExtension
import extension.GradleDslExtensionImpl
import extension.RootGradleDslExtension
import extension.RootGradleDslExtensionImpl
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Enhanced plugin core for gradle kotlin dsl.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class GradleDslXCore : Plugin<Any> {
  override fun apply(target: Any) {
    when (target) {
      is Settings -> target.bootstrap()
      is Project -> target.bootstrap()
    }
  }

  private fun Settings.bootstrap() = gradle.rootProject { bootstrap() }

  private fun Project.bootstrap() {
    if (this == rootProject) {
      extensions.add(GradleDslExtension::class, GradleDslXName, RootGradleDslExtensionImpl(this))
      subprojects { bootstrap() }
    } else {
      extensions.add(GradleDslExtension::class, GradleDslXName, GradleDslExtensionImpl(this))
    }

    afterEvaluate {
      kotlinCommonOptions {
        apiVersion = KotlinVersion
        languageVersion = KotlinVersion
        (this as? KotlinJvmOptions)?.jvmTarget = "1.8"
      }

      useExperimentalAnnotations("kotlin.RequiresOptIn")

      extensions.findByType<SourceSetContainer>()?.apply {
        findByName("main")?.java?.srcDirs("src/main/kotlin")
        findByName("test")?.java?.srcDirs("src/test/kotlin")
      }

      extensions.findByType<JavaPluginExtension>()?.apply {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
      }

      tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
      }

      tasks.withType<KotlinCompile> {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
      }

      extensions.configure<GradleDslExtension> {
        this as GradleDslExtensionImpl
        // Import declared dependencies directly
        (rootProject.extensions.findByType<GradleDslExtension>() as? RootGradleDslExtensionImpl)
          ?.sharedDependencies
          ?.get(scope ?: MainScope)
          ?.invoke(dependencies)
      }

      extensions.findByType<KotlinMultiplatformExtension>()?.apply {
        sourceSets.findByName("jvmMain")?.kotlin?.srcDirs("src/jvmMainShared/kotlin")
        sourceSets.findByName("androidMain")?.kotlin?.srcDirs("src/jvmMainShared/kotlin")

        sourceSets.findByName("jvmTest")?.kotlin?.srcDirs("src/jvmTestShared/kotlin")
        sourceSets.findByName("androidTest")?.kotlin?.srcDirs("src/jvmTestShared/kotlin")

        targets.all {
          if (this is KotlinAndroidTarget) {
            // Overwrite can be avoided in this case
            if (publishLibraryVariants.isNullOrEmpty()) publishAllLibraryVariants()
          }
        }
      }
    }
  }
}

/**
 * Use given [configuration] to configure extension of gradle settings.
 */
fun Settings.rootGradleDslX(configuration: RootGradleDslExtension.() -> Unit) {
  require(plugins.hasPlugin(GradleDslXCore::class)) {
    "Please apply the plugin first: `apply<GradleDslXCore>()` or `apply<GradleDslX>()`"
  }

  gradle.rootProject {
    extensions.configure<GradleDslExtension> {
      this as? RootGradleDslExtension ?: error(
        "Unexpected instance, the extension instance of the root project needs to be 'RootGradleDslExtension'."
      )
      configuration()
    }
  }
}

/**
 * Use given [configuration] to configure extension of root project gradle settings.
 */
fun Project.rootGradleDslX(configuration: RootGradleDslExtension.() -> Unit) {
  require(plugins.hasPlugin(GradleDslXCore::class)) {
    "Please apply the plugin first: `apply<GradleDslXCore>()` or `apply<GradleDslX>()`"
  }

  rootProject.extensions.configure<GradleDslExtension> {
    this as? RootGradleDslExtension ?: error(
      "Unexpected instance, the extension instance of the root project needs to be 'RootGradleDslExtension'."
    )
    configuration()
  }
}
