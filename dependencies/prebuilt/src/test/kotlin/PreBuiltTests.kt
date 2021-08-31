@file:Suppress("DEPRECATION")

import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import org.gradle.testfixtures.ProjectBuilder

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class PreBuiltTests : StringSpec({
  val tempProjectDir = tempdir()
  val project = ProjectBuilder.builder()
    .withProjectDir(tempProjectDir)
    .build()
  "mapping" {
    project.dependencyMapperPrebuilt()
    project.dependencyMapperPrebuilt()
    println("Mapped see $tempProjectDir")
  }
})