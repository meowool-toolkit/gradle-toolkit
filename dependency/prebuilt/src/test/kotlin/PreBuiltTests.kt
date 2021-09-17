@file:Suppress("DEPRECATION", "EXPERIMENTAL_IS_NOT_ENABLED")

import com.meowool.gradle.toolkit.DependencyMapperPlugin
import com.meowool.gradle.toolkit.internal.DependencyMapperExtensionImpl
import com.meowool.gradle.toolkit.internal.DependencyMapperExtensionImpl.Companion.cacheJson
import com.meowool.gradle.toolkit.internal.InternalGradleToolkitApi
import com.meowool.gradle.toolkit.internal.prebuilt.prebuilt
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import org.gradle.kotlin.dsl.apply
import org.gradle.testfixtures.ProjectBuilder

/**
 * @author 凛 (https://github.com/RinOrz)
 */
@InternalGradleToolkitApi
class PreBuiltTests : StringSpec({
  val tempProjectDir = tempdir()
  val project = ProjectBuilder.builder()
    .withProjectDir(tempProjectDir)
    .build()
  "test cache" {
    var json: String?
    DependencyMapperExtensionImpl(project).apply {
      prebuilt(
        libraries = "Deps",
        plugins = "PluginIds"
      )
      mapping()
      json = project.cacheJson.readText()
    }

    DependencyMapperExtensionImpl(project).apply {
      prebuilt(
        libraries = "Deps",
        plugins = "PluginIds"
      )
      json shouldBe toJson()
    }
  }
})