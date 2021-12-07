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
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
@file:Suppress("SpellCheckingInspection")

package prebuilt

import com.meowool.gradle.toolkit.internal.DependencyMapperExtensionImpl
import com.meowool.gradle.toolkit.internal.distinct
import com.meowool.sweekt.coroutines.size
import com.meowool.sweekt.datetime.minutes
import createCentralClient
import createGoogleClient
import createMvnClient
import io.kotest.assertions.retry
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.gradle.testfixtures.ProjectBuilder

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class ResultsCollectTests : FreeSpec({
  "client" - {
    "maven central" {
      val client = createCentralClient()
      requireCount(10900) {
        flowOf(
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
        ).map { client.fetchPrefixes(it) }.flattenMerge().size()
      }
    }

    "google maven" {
      val client = createGoogleClient()
      requireCount(627) {
        flowOf(
          "android",
          "androidx",
          "com.android",
        ).map { client.fetchPrefixes(it) }.flattenMerge().distinct().size()
      }
    }

    "mvnrepository" {
      val client = createMvnClient()
      requireCount(9) {
        flowOf(
          "mysql",
          "org.yaml",
        ).map { client.fetchGroups(it) }.flattenMerge().size()
      }
    }

    "maven central and google maven" {
      val google = createGoogleClient()
      val central = createCentralClient()
      requireCount(2700) {
        flowOf(
          google,
          central,
        ).map {
          it.fetchPrefixes("com.google")
        }.flattenMerge().distinct().size()
      }
    }
  }

  "integrated" - {
    val project = ProjectBuilder.builder()
      .withProjectDir(tempdir())
      .build()

    "maven central" {
      DependencyMapperExtensionImpl(project).run {
        libraries {
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
            requireResultAtLeast(10900)
          }
        }
        mapping()
      }
    }

    "mvnrepository" {
      DependencyMapperExtensionImpl(project).run {
        libraries {
          searchGroups(
            "mysql",
            "org.yaml",
          ) {
            fromMvnRepository()
            requireResultAtLeast(9)
          }
        }
        mapping()
      }
    }

    "google maven" {
      DependencyMapperExtensionImpl(project).run {
        libraries {
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
        mapping()
      }
    }

    "maven central and google maven" {
      DependencyMapperExtensionImpl(project).run {
        libraries {
          searchPrefixes("com.google") {
            fromGoogle()
            fromMavenCentral()
            requireResultAtLeast(2700)
          }
        }
        mapping()
      }
    }
  }
}) {
  companion object {
    suspend inline fun requireCount(min: Int, crossinline count: suspend () -> Int) {
      val sizes = mutableListOf<Int>()
      retry(Int.MAX_VALUE, timeout = 1.minutes) {
        val size = count()
        sizes += size
        sizes.maxOrNull()!! shouldBeGreaterThanOrEqual min
      }
      println("Results: ${sizes.maxOrNull()}")
    }
  }
}
