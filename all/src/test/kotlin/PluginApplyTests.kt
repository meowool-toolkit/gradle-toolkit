import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class PluginApplyTests : StringSpec({
  val projectDir = tempdir()

  "mapping" {
    val buildFile = projectDir.resolve("build.gradle").apply {
      writeText("""
        plugins { id "com.meowool.toolkit.gradle" }
      """.trimIndent())
    }
    projectDir.runGradle()
  }
}) {
  companion object {
    fun File.runGradle(): BuildResult = GradleRunner.create()
      .withProjectDir(this)
      .withPluginClasspath()
      .withDebug(true)
      .build()
  }

}