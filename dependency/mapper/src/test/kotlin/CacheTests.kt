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

import com.meowool.gradle.toolkit.internal.DefaultJson
import com.meowool.gradle.toolkit.internal.DependencyMapperExtensionImpl
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.gradle.testfixtures.ProjectBuilder

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class CacheTests : StringSpec({
  val project = ProjectBuilder.builder().withProjectDir(tempdir()).build()

  "test identity" {
    val original = DependencyMapperExtensionImpl(project).apply {
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
        search("remote")
        searchGroups("com.google.android") {
          fromGoogle()
          filter { false }
        }
        searchPrefixes("org.jetbrains", "org.apache") {
          fromMavenCentral()
          fromGradlePluginPortal()
        }
      }
      projects("PATHS")
    }
    val encoded = DefaultJson.encodeToString(original)
    val decoded = DefaultJson.decodeFromString<DependencyMapperExtensionImpl>(encoded)
    original shouldBe decoded
  }
})
