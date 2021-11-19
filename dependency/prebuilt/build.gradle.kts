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
@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED", "EXPERIMENTAL_API_USAGE_FUTURE_ERROR", "SpellCheckingInspection")
@file:OptIn(InternalGradleToolkitApi::class)

import com.meowool.gradle.toolkit.internal.DependencyMapperInternal
import com.meowool.gradle.toolkit.internal.InternalGradleToolkitApi

plugins { `kotlin-dsl` }

publication.data {
  artifactId = "toolkit-dependency-prebuilt"
  displayName = "Dependency Pre-Built for Gradle Toolkit"
  description = "Pre-Built commonly used dependencies."
}

dependencies {
  apiProject(Projects.Dependency.Mapper)
  implementationOf(
    Libs.Ktor.Client.Apache,
    Libs.Ktor.Client.Serialization,
    Libs.KotlinX.Serialization.Json,
  )
}

tasks.create("syncDependencies").doLast {
  DependencyMapperInternal.collectDependencies(
    project,
    destination = projectDir.resolve("src/main/resources/ci-dependencies.json")
  ) {
    concurrency(false)
    plugins {
      map(
        "com.github.johnrengelman.shadow" to "Shadow",
        "com.diffplug.spotless" to "Spotless",
        "com.gradle.publish" to "Gradle.Publish",
        "com.gradle.build-scan" to "Gradle.BuildScan",
        "me.tylerbwong.gradle.metalava" to "Gradle.Metalava",
        "org.gradle.crypto.checksum" to "Gradle.Crypto.Checksum",
        "org.gradle.android.cache-fix" to "Gradle.AndroidCacheFix",
      )

      searchPrefixes(
        "com.google",
        "org.gradle.kotlin",
        "com.gradle.enterprise",

        "org.jetbrains.dokka",
        "org.jetbrains.gradle",
        "org.jetbrains.kotlin",
        "org.jetbrains.intellij",
        "org.jetbrains.changelog",
      ) {
        fromGradlePluginPortal()
        requireResultAtLeast(7)
      }
    }
    libraries {
      map(
        "org.zeroturnaround:zt-zip" to "ZtZip",
        "com.tfowl.ktor:ktor-jsoup" to "Ktor.Jsoup",
        "com.github.promeg:tinypinyin" to "TinyPinyin",
        "in.arunkumarsampath:transition-x" to "TransitionX",
        "com.github.ben-manes.caffeine:caffeine" to "Caffeine",
        "de.fayard.refreshVersions:refreshVersions" to "RefreshVersions",
        "com.andkulikov:transitionseverywhere" to "TransitionsEverywhere",
        "com.github.donkingliang:ConsecutiveScroller" to "ConsecutiveScroller",

        "me.tylerbwong.gradle:metalava-gradle" to "Gradle.Metalava",
        "com.diffplug.spotless:spotless-plugin-gradle" to "Gradle.Spotless",
        "com.gradle.publish:plugin-publish-plugin" to "Gradle.Publish.Plugin",
      )

      searchGroups(
        "mysql",
        "org.yaml",
      ) {
        fromMvnRepository()
        requireResultAtLeast(9)
      }

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
        "com.soywiz.korlibs",
        "com.linkedin.dexmaker",
        "com.github.ajalt.clikt",

        "org.ow2.asm",
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
        requireResultAtLeast(7800)
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
        requireResultAtLeast(2700)
      }

      searchPrefixes(
        "android",
        "androidx",
        "com.android",
      ) {
        fromGoogle()
        requireResultAtLeast(627)
        // Skip deprecated dependencies
        filterNot { it.startsWith("com.android.support") }
      }
    }
  }
}
