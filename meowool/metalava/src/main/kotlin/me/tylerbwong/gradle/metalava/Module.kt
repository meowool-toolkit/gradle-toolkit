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
package me.tylerbwong.gradle.metalava

import com.android.build.gradle.LibraryExtension
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File

internal sealed class Module {

  open val bootClasspath: Collection<File> = emptyList()
  abstract val compileClasspath: Collection<File>

  class Android(private val extension: LibraryExtension, private val variantName: String) : Module() {
    override val bootClasspath: Collection<File>
      get() = extension.bootClasspath
    override val compileClasspath: Collection<File>
      get() = extension.libraryVariants.find {
        it.name.contains(variantName, ignoreCase = true)
      }?.getCompileClasspath(null)?.filter { it.exists() }?.files ?: emptyList()
  }

  class Multiplatform(private val extension: KotlinMultiplatformExtension) : Module() {
    override val compileClasspath: Collection<File>
      get() = extension.targets
        .flatMap { it.compilations }
        .filter { it.defaultSourceSetName.contains("main", ignoreCase = true) }
        .flatMap { it.compileDependencyFiles }
        .filter { it.exists() && it.checkDirectory(listOf(".jar", ".class")) }
  }

  class Java(private val extension: JavaPluginExtension) : Module() {
    override val bootClasspath: Collection<File>
      get() = File(System.getProperty("java.home")).walkTopDown()
        .toList()
        .filter { it.exists() && it.name == "rt.jar" }
    override val compileClasspath: Collection<File>
      get() = extension.sourceSets
        .filter { it.name.contains("main", ignoreCase = true) }
        .flatMap { it.compileClasspath }
        .filter { it.exists() && it.checkDirectory(listOf(".jar", ".class")) }
  }

  companion object {
    internal fun Project.module(extension: MetalavaExtension): Module? {
      // Use findByName to avoid requiring consumers to have the Android Gradle plugin
      // in their classpath when applying this plugin to a non-Android project
      val libraryExtension = extensions.findByName("android")
      val multiplatformExtension = extensions.findByName("kotlin")
      val javaPluginExtension = extensions.findByType<JavaPluginExtension>()
      return when {
        libraryExtension != null && libraryExtension is LibraryExtension ->
          Android(libraryExtension, extension.androidVariantName)

        multiplatformExtension != null && multiplatformExtension is KotlinMultiplatformExtension ->
          Multiplatform(multiplatformExtension)

        javaPluginExtension != null -> Java(javaPluginExtension)

        else -> if (extension.ignoreUnsupportedModules) null else throw GradleException("This module is currently not supported by the Metalava plugin")
      }
    }

    internal fun File.checkDirectory(validExtensions: Collection<String>): Boolean {
      return if (isFile) {
        validExtensions.any { name.endsWith(it, ignoreCase = true) }
      } else {
        listFiles()?.all { it.checkDirectory(validExtensions) } ?: false
      }
    }
  }
}
