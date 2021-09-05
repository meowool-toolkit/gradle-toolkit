package com.meowool.gradle.toolkit

import addIfNotExists
import com.meowool.gradle.toolkit.internal.GradleToolkitExtensionImpl
import com.meowool.gradle.toolkit.internal.KotlinApiVersion
import com.meowool.gradle.toolkit.internal.KotlinJvmTarget
import com.meowool.gradle.toolkit.internal.KotlinLanguageVersion
import kotlinJvmOptions
import kotlinOptions
import optIn
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
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
      afterEvaluate {
        optIn("kotlin.RequiresOptIn")

        kotlinOptions {
          apiVersion = KotlinApiVersion
          languageVersion = KotlinLanguageVersion
        }

        kotlinJvmOptions {
          @Suppress("DEPRECATION")
          useIR = true
          jvmTarget = KotlinJvmTarget
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
}