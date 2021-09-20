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
import com.meowool.gradle.toolkit.DependencyMapperExtension
import com.meowool.gradle.toolkit.internal.DependencyMapperExtensionImpl
import com.meowool.gradle.toolkit.internal.DependencyMapperExtensionImpl.Companion.CacheDir
import com.meowool.gradle.toolkit.internal.DependencyMapperExtensionImpl.Companion.CacheJarsDir
import com.meowool.gradle.toolkit.internal.concurrentFlow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.toList
import org.gradle.testfixtures.ProjectBuilder

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class MappingTests : FreeSpec({
  val project = ProjectBuilder.builder()
    .withProjectDir(tempdir())
    .build()

  fun runMapping(block: DependencyMapperExtension.() -> Unit): JarClassLoader =
    DependencyMapperExtensionImpl(project).run {
      block()
      mapping()
      JarClassLoader(*project.projectDir.resolve("$CacheDir/$CacheJarsDir").listFiles()!!)
    }

  "libraries" - {
    "declared" {
      val fields = runMapping {
        libraries(LibrariesRoot) {
          map(
            "a:b",
            "foo:bar",
            "foo:bar-baz",
            "foo:bar_baz",
            "foo:bar.baz",
            "com.typesafe.akka:akka-actor",
            "com.google.inject:jdk8-tests",
            "com.google.inject.extensions:guice-dagger-adapter",
          )
        }
      }.findClass(LibrariesRoot).collectFields()
      fields shouldContainAll listOf(
        Field(
          holder = "$LibrariesRoot.A",
          name = "B",
          value = "a:b:_"
        ),
        Field(
          holder = "$LibrariesRoot.Foo",
          name = "Bar",
          value = "foo:bar:_"
        ),
        Field(
          holder = "$LibrariesRoot.Com.Typesafe.Akka",
          name = "Actor",
          value = "com.typesafe.akka:akka-actor:_"
        ),
        Field(
          holder = "$LibrariesRoot.Com.Google.Inject.Jdk8",
          name = "Tests",
          value = "com.google.inject:jdk8-tests:_"
        ),
        Field(
          holder = "$LibrariesRoot.Com.Google.Inject.Extensions.Guice.Dagger",
          name = "Adapter",
          value = "com.google.inject.extensions:guice-dagger-adapter:_"
        ),
      )
      fields.filter {
        it.value == "foo:bar-baz:_" ||
          it.value == "foo:bar_baz:_" ||
          it.value == "foo:bar.baz:_"
      }.all {
        it.holder == "$LibrariesRoot.Foo.Bar" && it.name.startsWith("Baz")
      }.shouldBeTrue()
    }

    "remote" {
      infix fun List<*>.compareWith(other: List<*>) {
        withClue("this to other") { this shouldContainAll other }
        withClue("other to this") { other shouldContainAll this }
      }
      val dependencies = createCentralClient().fetch("compose").toList()
      runMapping {
        libraries(LibrariesRoot) { map(dependencies) }
      }.findClass(LibrariesRoot)
        .collectFields()
        .map { it.value.removeSuffix(":_") } compareWith dependencies.map { it.toString() }
    }
  }

  "projects" {
    runMapping {
      projects(ProjectsRoot) {
        mapRootProject("main-bar")
      }
    }.findClass(ProjectsRoot).collectFields() shouldBe listOf(
      Field(
        holder = "$ProjectsRoot.Main",
        name = "Bar",
        value = ":"
      )
    )
  }

  "plugins and libraries" {
    val classLoader = runMapping {
      val plugins = plugins(PluginsRoot) {
        map(
          "com.plugin.id",
          "pp",
        )
      }
      libraries(LibrariesRoot) {
        transferPluginIds(plugins)
        map(
          "foo:bar",
          "foo.bar.plugin:foo.bar.plugin.gradle.plugin",
        )
      }
    }
    // Validate library notations
    classLoader.findClass(LibrariesRoot).collectFields() shouldBe listOf(
      Field(
        holder = "$LibrariesRoot.Foo",
        name = "Bar",
        value = "foo:bar:_"
      ),
      Field(
        holder = "$LibrariesRoot.Foo.Bar.Plugin.Gradle",
        name = "Plugin",
        value = "foo.bar.plugin:foo.bar.plugin.gradle.plugin:_"
      ),
    )
    // Validate plugin id
    classLoader.findClass(PluginsRoot).collectFields() shouldContainExactlyInAnyOrder listOf(
      Field(
        holder = "$PluginsRoot.Foo.Bar",
        name = "Plugin",
        value = "foo.bar.plugin"
      ),
      Field(
        holder = "$PluginsRoot.Com.Plugin",
        name = "Id",
        value = "com.plugin.id"
      ),
      Field(
        holder = PluginsRoot,
        name = "Pp",
        value = "pp"
      ),
    )
  }
}) {
  private companion object {
    const val LibrariesRoot = "Deps"
    const val PluginsRoot = "PluginIds"
    const val ProjectsRoot = "Paths"

    data class Field(
      val holder: String,
      val name: String,
      val value: String,
    )

    /**
     * class Foo {
     *   val Bar = "";
     *   ...
     *
     *   class Nested {
     *     ...
     *   }
     * }
     */
    suspend fun Class<*>.collectFields(): List<Field> {
      fun Class<*>.impl(): Flow<Field> = concurrentFlow {
        declaredFields.forEachConcurrently {
          send(Field(it.declaringClass.canonicalName, it.name, it.get(null).toString()))
        }
        declaredClasses.forEachConcurrently {
          sendAll(it.impl())
        }
      }
      return impl().toList()
    }
  }
}
