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