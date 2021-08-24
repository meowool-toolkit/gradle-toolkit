import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import org.gradle.testfixtures.ProjectBuilder
import java.io.File

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class PreBuiltTests : StringSpec({
  val tempProjectDir = tempdir()
  val project = ProjectBuilder.builder()
    .withProjectDir(tempProjectDir)
    .build()
  "mapping" {
    DependencyMapper(project).builtIn().mapping()
    println("Mapped see $tempProjectDir")
  }
})