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

import extension.GradleToolkitExtension
import extension.GradleToolkitExtensionImpl
import extension.RootGradleToolkitExtension
import extension.RootGradleToolkitExtensionImpl
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
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Enhanced plugin core for gradle kotlin dsl.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class GradleToolkitCore : Plugin<Any> {
  override fun apply(target: Any) {
    when (target) {
      is Settings -> target.gradle.rootProject { bootstrap() }
      is Project -> target.bootstrap()
    }
  }

  private fun Project.bootstrap() {
    if (this == rootProject) {
      addExtension { RootGradleToolkitExtensionImpl(this) }
      subprojects { bootstrap() }
    } else {
      addExtension { GradleToolkitExtensionImpl(this) }
    }

    afterEvaluate {
      optIn("kotlin.RequiresOptIn")

      kotlinOptions {
        apiVersion = KotlinApiVersion
        languageVersion = KotlinLanguageVersion
      }

      kotlinJvmOptions {
        jvmTarget = "1.8"
      }

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

      extensions.configure<GradleToolkitExtension> {
        this as GradleToolkitExtensionImpl
        // Import declared dependencies directly
        (rootProject.extensions.findByType<GradleToolkitExtension>() as? RootGradleToolkitExtensionImpl)
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

  private inline fun Project.addExtension(extension: () -> GradleToolkitExtension) =
    extensions.addIfNotExists(GradleToolkitExtension::class, GradleToolkitName, extension)
}

/**
 * Use given [configuration] to configure root toolkit extension of gradle settings.
 */
inline fun Settings.rootGradleToolkit(crossinline configuration: RootGradleToolkitExtension.() -> Unit) {
  require(plugins.hasPlugin<GradleToolkitCore>()) {
    "Please apply the plugin first: `apply<GradleToolkitCore>()` or `apply<GradleToolkit>()`"
  }

  gradle.rootProject {
    extensions.configure<GradleToolkitExtension> {
      this as? RootGradleToolkitExtension ?: error(
        "Unexpected instance, the extension instance of the root project needs to be 'RootGradleToolkitExtension'."
      )
      configuration()
    }
  }
}

/**
 * Use given [configuration] to configure root toolkit extension of gradle project.
 */
inline fun Project.rootGradleToolkit(crossinline configuration: RootGradleToolkitExtension.() -> Unit) {
  require(plugins.hasPlugin<GradleToolkitCore>()) {
    "Please apply the plugin first: `apply<GradleToolkitCore>()` or `apply<GradleToolkit>()`"
  }

  rootProject.extensions.configure<GradleToolkitExtension> {
    this as? RootGradleToolkitExtension ?: error(
      "Unexpected instance, the extension instance of the root project needs to be 'RootGradleToolkitExtension'."
    )
    configuration()
  }
}
