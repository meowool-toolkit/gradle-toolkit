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
@file:Suppress("EXPERIMENTAL_API_USAGE")

import com.meowool.gradle.toolkit.internal.DependencyMapperExtensionImpl
import com.meowool.gradle.toolkit.internal.DependencyMapperInternal.CacheDir
import com.meowool.gradle.toolkit.internal.DependencyMapperInternal.CacheJarsDir
import io.kotest.assertions.forEachAsClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import org.gradle.testfixtures.ProjectBuilder

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class CacheTests : StringSpec({
  val project = ProjectBuilder.builder().withProjectDir(tempdir()).build()
  val dependencyMapper = DependencyMapperExtensionImpl(project).apply {
    val p = plugins("PluginMapped")
    libraries("Libraries") {
      transferPluginIds(p)
      map("foo:bar", "a.B.C:dd")
      map(
        "com.foo.bar:v" to "Foo",
        "test:test.plugin" to "Test.Plugin"
      )
      searchDefaultOptions {
        filter { true }
      }
      search("meowool")
      searchGroups("com.google.android") {
        fromGoogle()
        filter { false }
      }
      searchPrefixes("org.jetbrains.anko", "org.apache.avro") {
        fromMavenCentral()
        fromGradlePluginPortal()
      }
    }
    projects("PATHS")
  }

  fun checkUsingCache() {
    dependencyMapper.mapping() shouldBe false
  }

  fun checkRemapping() {
    dependencyMapper.mapping() shouldBe true
  }

  "initial cache" {
    checkRemapping()
  }

  "remapping" {
    println("==============  using cache  ==============")
    checkUsingCache()
    dependencyMapper.apply {
      libraries("Libraries") {
        map("new.group:id")
      }
    }
    checkRemapping()

    println("==============  using cache  ==============")
    checkUsingCache()
    dependencyMapper.apply {
      plugins("AdditionalPlugins") {
        map("addition.plugin")
      }
    }
    checkRemapping()
  }

  "clear cache" {
    checkUsingCache()

    // Ensure that some cache names remain unchanged
    val cacheJarsDir = project.projectDir.resolve("$CacheDir/$CacheJarsDir")
    val cacheJars = cacheJarsDir.listFiles()!!

    // Clear `AdditionalPlugins`
    cacheJars.filter { it.name.startsWith("AdditionalPlugins") }.forEach { it.delete() }

    checkRemapping()

    val newCacheJars = cacheJarsDir.listFiles()!!

    // Only `AdditionalPlugins` re-cached
    newCacheJars.filter { new ->
      // Filter out names that do not exist in the old `cacheJars`
      cacheJars.none { old -> new == old }
    }.forEachAsClue { it.name.shouldStartWith("AdditionalPlugins") }
  }
})
