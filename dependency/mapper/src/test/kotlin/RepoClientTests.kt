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

import com.meowool.gradle.toolkit.LibraryDependency
import com.meowool.gradle.toolkit.PluginId
import com.meowool.gradle.toolkit.internal.client.DependencyRepositoryClient
import com.meowool.gradle.toolkit.internal.client.MvnRepositoryClient
import com.meowool.gradle.toolkit.internal.flatMapConcurrently
import com.meowool.sweekt.coroutines.contains
import com.meowool.sweekt.coroutines.flowOnIO
import com.meowool.sweekt.coroutines.size
import com.meowool.sweekt.iteration.contains
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.scopes.FreeSpecContainerContext
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import org.jsoup.nodes.Document

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class RepoClientTests : FreeSpec({
  "from google maven repository" - {
    val google = createGoogleClient()
    "fetch not found" {
      google.getOrNull<Document>("abcdefg/group-index.xml").shouldBeNull()
    }
    "fetch all" {
      google.fetchAllDependencies()
        .filter { it.equals("androidx.annotation:annotation-experimental-lint") }
        .flowOnIO().size() shouldBe 1
    }
    "fetch by group" {
      val groups = flowOf("androidx", "com.android")
      groups.flatMapConcurrently { google.fetchGroups(it) }
        .filterNot { dep -> groups.toList().any { dep.group == it } }
        .flowOnIO()
        .size()
        .shouldBeZero()
    }
    "fetch by `startsWith`" {
      val starts = flowOf("androidx", "com.android")
      starts.flatMapConcurrently { google.fetchStartsWith(it) }
        .flowOnIO()
        .filterNot { dep -> starts.toList().any { dep.startsWith(it) } }
        .size()
        .shouldBeZero()
    }
  }

  "from gradle plugin portal" - {
    val client = createGradlePluginClient()
    "fetch by keyword" {
      client.fetch("meowool")
        .filter { it.equals("com.meowool.toolkit.gradle-dsl-x-core:com.meowool.toolkit.gradle-dsl-x-core.gradle.plugin") }
        .flowOnIO().size() shouldBe 1
    }
    "fetch by group" {
      client.fetchGroups("com.meowool")
        .flowOnIO()
        .size()
        .shouldBeZero()
    }
    "fetch by `startsWith`" {
      client.fetchStartsWith("com.meowool").flowOnIO().toList().also {
        it shouldContain PluginId("com.meowool.toolkit.gradle-dsl-x").toLibraryDependency()
        it shouldContain LibraryDependency("com.meowool.toolkit.gradle-dsl-x-core:com.meowool.toolkit.gradle-dsl-x-core.gradle.plugin")
      }
    }
  }

  suspend fun FreeSpecContainerContext.commonTest(client: DependencyRepositoryClient) {
    "fetch by keyword" {
      client.fetch("javassist").flowOnIO().toList().apply {
        this shouldContain LibraryDependency("org.javassist:javassist")
        if (client is MvnRepositoryClient) {
          this shouldContain LibraryDependency("com.googlecode.jmapper-framework:jmapper-core")
          this shouldContain LibraryDependency("jaop.domain:domain")
        }
      }
      client.fetch("coil").flowOnIO().size() shouldBeGreaterThanOrEqual if (client is MvnRepositoryClient) 26 else 19
    }
    "fetch by group" {
      val groups = flowOf("io.reactivex", "org.scala-lang")
      groups.flatMapConcurrently { client.fetchGroups(it) }.flowOnIO().toList().apply {
        // Contains
        if (client is MvnRepositoryClient) {
          this shouldContain LibraryDependency("org.scala-lang:scala3-library")
        } else {
          this shouldContain LibraryDependency("org.scala-lang:scala3-library_3")
        }
        this shouldContain LibraryDependency("io.reactivex:rxjava")
        // Not contains
        contains { it.startsWith("org.scala-lang.modules") }.shouldBeFalse()
        contains { it.startsWith("io.reactivex.rxjava2") }.shouldBeFalse()
        // All groups are matched
        all { groups.contains { g -> g == it.group } }.shouldBeTrue()
      }
    }
    "fetch by `startsWith`" {
      flowOf("io.reactivex", "org.scala-lang")
        .flatMapConcurrently { client.fetchStartsWith(it) }
        .flowOnIO().toList().apply {
          forEach { println(it) }
          if (client is MvnRepositoryClient) {
            this shouldContain LibraryDependency("org.scala-lang.modules:scala-collection-compat")
          } else {
            this shouldContain LibraryDependency("org.scala-lang.modules:scala-collection-compat_3")
          }
          this shouldContain LibraryDependency("io.reactivex.rxjava2:rxjava")
          this.all { it.startsWith("io.reactivex") || it.startsWith("org.scala-lang") }
        }
    }
  }

  "from mvn repository" - {
    commonTest(createMvnClient(fetchExactly = false))
    "fetch exactly by group" {
      createMvnClient(fetchExactly = true).fetchGroups("org.scala-lang.modules").flowOnIO().toList().also {
        it shouldContain LibraryDependency("org.scala-lang.modules:scala-collection-compat_3")
        it shouldContain LibraryDependency("org.scala-lang.modules:scala-xml_3")
        it shouldNotContain LibraryDependency("org.scala-lang.modules:scala-xml")
      }
    }
  }

  "from maven repository" - {
    commonTest(createCentralClient())
  }
})
