@file:Suppress("DEPRECATION", "EXPERIMENTAL_IS_NOT_ENABLED")

import annotation.InternalGradleToolkitApi
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import org.gradle.testfixtures.ProjectBuilder

/**
 * @author 凛 (https://github.com/RinOrz)
 */
@OptIn(InternalGradleToolkitApi::class)
class PreBuiltTests : StringSpec({
  val tempProjectDir = tempdir()
  val project = ProjectBuilder.builder()
    .withProjectDir(tempProjectDir)
    .build()
  "mapping" {
    DependencyMapperExtensionImpl(project).prebuilt().mapping()
    println("Mapped see $tempProjectDir")
  }
})