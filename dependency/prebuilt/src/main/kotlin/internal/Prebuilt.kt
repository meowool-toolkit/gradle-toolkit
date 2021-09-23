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
@file:Suppress("SpellCheckingInspection", "NAME_SHADOWING", "DEPRECATION")

package com.meowool.gradle.toolkit.internal.prebuilt

import com.meowool.gradle.toolkit.DependencyMapperExtension
import com.meowool.gradle.toolkit.LibraryDependencyDeclaration
import com.meowool.gradle.toolkit.PluginDependencyDeclaration
import com.meowool.gradle.toolkit.ProjectDependencyDeclaration
import com.meowool.gradle.toolkit.internal.DependencyMapperInternal.CacheDir
import com.meowool.gradle.toolkit.internal.DependencyMapperInternal.DependencyOutputList
import io.ktor.client.HttpClient
import io.ktor.client.features.json.serializer.KotlinxSerializer.Companion.DefaultJson
import io.ktor.client.request.get
import io.ktor.utils.io.core.use
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.decodeFromStream
import org.gradle.api.Project

/**
 * The prebuilt dependency JSON, this list is continuously updated through CI by default to ensure that the mapped
 * dependencies are all up to date.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal object PrebuiltList {
  private const val CacheFileName = "ci-dependencies.json"
  private const val RemoteUrl = "https://raw.githubusercontent.com/meowool-toolkit/gradle-toolkit/main/dependency/prebuilt/src/main/resources/$CacheFileName"
  private val Project.listCache
    get() = project.projectDir.resolve("$CacheDir/$CacheFileName").apply { parentFile.mkdirs() }

  suspend fun fromRemote(project: Project): DependencyOutputList = HttpClient().use {
    val result = it.get<String>(RemoteUrl)
    project.listCache.writeText(result)
    DefaultJson.decodeFromString(result)
  }

  fun fromCacheOrBundle(project: Project, remoteE: Exception): DependencyOutputList = DefaultJson.decodeFromStream(
    project.listCache.takeIf { it.exists() }?.inputStream()
      ?: PrebuiltList::class.java.getResourceAsStream("/ci-dependencies.json")
      ?: error("Prebuilt JSON ($RemoteUrl) gets failed: ${remoteE.message}")
  )

  suspend fun get(project: Project): DependencyOutputList = try {
    fromRemote(project)
  } catch (e: Exception) {
    fromCacheOrBundle(project, e)
  }
}

internal fun <T : DependencyMapperExtension> T.prebuilt(
  libraries: String = LibraryDependencyDeclaration.DefaultRootClassName,
  projects: String = ProjectDependencyDeclaration.DefaultRootClassName,
  plugins: String = PluginDependencyDeclaration.DefaultRootClassName,
) = runBlocking(Dispatchers.IO) {
  val dependenciesCI = PrebuiltList.get(project)

  plugins(plugins) {
    // Results from CI search or resource
    map(dependenciesCI.plugins)
    map(dependenciesCI.mappedPlugins)
  }
  libraries(libraries) {
    transferPluginIds(plugins)

    // Results from CI search or resource
    map(dependenciesCI.libraries)
    map(dependenciesCI.mappedLibraries)
  }
  projects(projects)

  format {
    notCapitalize { name ->
      // Ignore specific platform name
      name.startsWith("ios", ignoreCase = true) ||
        name.startsWith("wasm32", ignoreCase = true) ||
        name.startsWith("wasm64", ignoreCase = true)
    }
    onEachName {
      when (it) {
        // Exact replacement
        "Io" -> "IO"
        "ios", "IOS" -> "iOS"
        else ->
          it
            .replace("androidx", "AndroidX")
            .replace("kotlinx", "KotlinX")
            .replace("viewmodel", "ViewModel")
            .replace("uiautomator", "UiAutomator")
            .replace("janktesthelper", "JanktestHelper")
            .replace("constraintlayout", "ConstraintLayout")
            .replace("viewbinding", "ViewBinding")
            .replace("databinding", "DataBinding")
            .replace("bundletool", "BundleTool")
            .replace("signflinger", "SignFlinger")
            .replace("zipflinger", "ZipFlinger")
            .replace("vectordrawable", "VectorDrawable")
            .replace("mingwx", "Mingw.X")
            .replace("mingwarm", "Mingw.Arm")
            .replace("linuxx", "Linux.X")
            .replace("linuxarm", "Linux.Arm")
            .replace("macosx", "MacOS.X")
            .replace("macosarm", "MacOS.Arm")
            .replace("iosx", "iOS.X")
            .replace("iosarm", "iOS.Arm")
            .replace("tvosx", "TvOS.X")
            .replace("tvosarm", "TvOS.Arm")
            .replace("wasm32", "wArm32")
            .replace("wasm64", "wArm64")
            .replace("watchosx", "WatchOS.X")
            .replace("watchosarm", "WatchOS.Arm")
            .replace("okhttp", "OkHttp")
            .replace("bytebuddy", "ByteBuddy")
      }
    }
    onStart {
      val it = it
        .replace("org.jetbrains.kotlin", "kotlin")
        .replace("org.jetbrains.intellij", "intellij")
        .replace("com.google.android", "google")
        .replace("org.chromium.net", "chromium")
        .replace("com.squareup", "square")
        .replace("io.arrow-kt", "arrow")
        .replace("app.cash", "CashApp")
        .replace("io.coil-kt", "Coil")
        .replace("android.tools.build.gradle", "Android.Gradle.Plugin")
        .removePrefix("com.github.ajalt.")
        .removePrefix("com.linkedin.")
        .removePrefix("com.afollestad.")
        .removePrefix("me.liuwj.")
        .removePrefix("io.github.")
      when {
        it.startsWith("com.", ignoreCase = true) -> it.removePrefixFuzzy("com.")
        it.startsWith("net.", ignoreCase = true) -> it.removePrefixFuzzy("net.")
        it.startsWith("cn.", ignoreCase = true) -> it.removePrefixFuzzy("cn.")
        it.startsWith("org.", ignoreCase = true) -> it.removePrefixFuzzy("org.")
        it.startsWith("top.", ignoreCase = true) -> it.removePrefixFuzzy("top.")
        it.startsWith("edu.", ignoreCase = true) -> it.removePrefixFuzzy("edu.")
        it.startsWith("ink.", ignoreCase = true) -> it.removePrefixFuzzy("ink.")
        it.startsWith("info.", ignoreCase = true) -> it.removePrefixFuzzy("info.")
        it.startsWith("pro.", ignoreCase = true) -> it.removePrefixFuzzy("pro.")
        it.startsWith("io.", ignoreCase = true) -> it.removePrefixFuzzy("io.")
        it.startsWith("me.", ignoreCase = true) -> it.removePrefixFuzzy("me.")
        else -> it
      }
    }
  }
}

/** com or Com or COM */
private fun String.removePrefixFuzzy(prefix: String) = removePrefix(prefix)
  .removePrefix(prefix.capitalize())
  .removePrefix(prefix.toUpperCase())
