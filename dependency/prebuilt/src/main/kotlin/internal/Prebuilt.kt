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
 * 除如果您正在修改此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
@file:Suppress("SpellCheckingInspection", "NAME_SHADOWING", "DEPRECATION")

package com.meowool.gradle.toolkit.internal.prebuilt

import com.meowool.gradle.toolkit.DependencyMapperExtension
import com.meowool.gradle.toolkit.LibraryDependencyDeclaration
import com.meowool.gradle.toolkit.PluginDependencyDeclaration
import com.meowool.gradle.toolkit.ProjectDependencyDeclaration

internal fun <T : DependencyMapperExtension> T.prebuilt(
  libraries: String = LibraryDependencyDeclaration.DefaultRootClassName,
  projects: String = ProjectDependencyDeclaration.DefaultRootClassName,
  plugins: String = PluginDependencyDeclaration.DefaultRootClassName,
) = apply {
  plugins(plugins) {
    searchPrefixes(
      "com.google",

      "org.jetbrains.dokka",
      "org.jetbrains.gradle",
      "org.jetbrains.kotlin",
      "org.jetbrains.intellij",
      "org.jetbrains.changelog",
    ) { fromGradlePluginPortal() }
  }

  libraries(libraries) {
    transferPluginIds(plugins)
    map(
      "com.tfowl.ktor:ktor-jsoup" to "Ktor.Jsoup",
      "com.github.ben-manes.caffeine:caffeine" to "Caffeine",
      "com.github.promeg:tinypinyin" to "TinyPinyin",
      "de.fayard.refreshVersions:refreshVersions" to "RefreshVersions",
      "com.github.donkingliang:ConsecutiveScroller" to "ConsecutiveScroller",
      "org.zeroturnaround:zt-zip" to "ZtZip",
      "com.andkulikov:transitionseverywhere" to "TransitionsEverywhere",
      "in.arunkumarsampath:transition-x" to "TransitionX",
      "com.diffplug.spotless:spotless-plugin-gradle" to "Gradle.Spotless",
      "com.gradle.publish:plugin-publish-plugin" to "Gradle.Publish.Plugin"
    )

    // Do not use the following method, because it will receive additional options by the user
    // searchDefaultOptions { fromMavenCentral() }

    searchGroups(
      "mysql",
      "org.yaml",
    ) { fromMavenCentral() }

    searchPrefixes(
      "app.cash",
      "net.mamoe",
      "net.bytebuddy",
      "me.liuwj.ktorm",

      // Apache
      "commons-io",
      "commons-logging",

      "org.apache.tika",
      "org.apache.hbase",
      "org.apache.hadoop",
      "org.apache.commons",
      "org.apache.logging.log4j",

      "com.umeng",
      "com.airbnb",
      "com.rinorz",
      "com.tencent",
      "com.meowool",
      "com.firebase",
      "com.facebook",
      "com.squareup",
      "com.yalantis",
      "com.facebook",
      "com.afollestad",
      "com.didiglobal",
      "com.jakewharton",
      "com.linkedin.dexmaker",
      "com.github.ajalt.clikt",

      "org.ow2",
      "org.junit",
      "org.smali",
      "org.jsoup",
      "org.mockito",
      "org.javassist",
      "org.conscrypt",
      "org.robolectric",
      "org.springframework",
      "org.spekframework.spek2",

      "io.ktor",
      "io.mockk",
      "io.kotest",
      "io.strikt",
      "io.coil-kt",
      "io.arrow-kt",
      "io.insert-koin",
      "io.github.reactivecircus",
      "io.github.javaeden.orchid",

      "org.jetbrains.markdown",
      "org.jetbrains.annotations",
      "org.jetbrains.kotlin",
      "org.jetbrains.kotlinx",
      "org.jetbrains.compose",
      "org.jetbrains.dokka",
      "org.jetbrains.exposed",
      "org.jetbrains.kotlin-wrappers",
      "org.jetbrains.intellij",
      "org.jetbrains.anko",
      "org.jetbrains.spek",
      "org.jetbrains.lets-plot",
      "org.jetbrains.skiko",
      "org.jetbrains.teamcity",
    ) {
      fromMavenCentral()
      // Skip deprecated dependencies
      filterNot {
        it.group == "com.squareup.okhttp" ||
          it.artifact == "kotlinx-serialization-runtime-jsonparser" ||
          it.startsWith("com.meowool.toolkit:gradle-dsl-x")
      }
    }

    searchPrefixes(
      "com.google",
    ) {
      fromMavenCentral()
      fromGoogle()
    }

    searchPrefixes(
      "android",
      "androidx",
      "com.android",
    ) {
      fromGoogle()
      // Skip deprecated dependencies
      filterNot { it.startsWith("com.android.support") }
    }
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
        .replace("app.cash", "CashApp")
        .replace("io.coil-kt", "Coil")
        .replace("io.arrow-kt", "Arrow")
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
