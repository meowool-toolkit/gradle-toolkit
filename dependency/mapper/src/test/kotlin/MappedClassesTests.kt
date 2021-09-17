import com.meowool.gradle.toolkit.DependencyMapperExtension
import com.meowool.gradle.toolkit.internal.DependencyMapperExtensionImpl
import com.meowool.gradle.toolkit.internal.DependencyMapperExtensionImpl.Companion.cacheJar
import com.meowool.gradle.toolkit.internal.forEachConcurrently
import com.meowool.gradle.toolkit.internal.sendAll
import com.meowool.sweekt.iteration.size
import io.kotest.assertions.withClue
import io.kotest.core.TestConfiguration
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeSameSizeAs
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
class MappedClassesTests : FreeSpec({
  val project = ProjectBuilder.builder()
    .withProjectDir(tempdir())
    .build()

  fun runMapping(block: DependencyMapperExtension.() -> Unit): JarClassLoader {
    DependencyMapperExtensionImpl(project).apply(block).mapping()
    return JarClassLoader(project.cacheJar.readText())
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

    "declared with plugin id" {
      val classLoader = runMapping {
        val plugins = plugins(PluginsRoot)
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
      classLoader.findClass(PluginsRoot).collectFields() shouldBe listOf(
        Field(
          holder = "$PluginsRoot.Foo.Bar",
          name = "Plugin",
          value = "foo.bar.plugin"
        ),
      )
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
    }.findClass(ProjectsRoot).collectFields() shouldBe listOf(Field(
      holder = "$ProjectsRoot.Main",
      name = "Bar",
      value = ":"
    ))
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
      fun Class<*>.impl(): Flow<Field> = channelFlow {
        declaredFields.forEachConcurrently {
          send(Field(it.declaringClass.canonicalName, it.name, it.get(null).toString()))
        }
        declaredClasses.forEach {
          sendAll { it.impl() }
        }
      }
      return impl().toList()
    }
  }
}