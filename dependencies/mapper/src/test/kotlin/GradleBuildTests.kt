import GradleScriptModifier.Companion.Build
import GradleScriptModifier.Companion.BuildKts
import GradleScriptModifier.Companion.Settings
import GradleScriptModifier.Companion.SettingsKts
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File


/**
 * @author 凛 (https://github.com/RinOrz)
 */
class GradleBuildTests : StringSpec({
  val projectDir = tempdir()
  val settingsFile = projectDir.resolve(Settings)
  val buildKtsFile = projectDir.resolve(BuildKts)
  val settingsKtsFile = projectDir.resolve(SettingsKts)

  "mapping" {
    val buildFile = projectDir.resolve(Build).apply {
      writeText("""
        plugins { id "com.meowool.gradle.toolkit-deps-mapper" }
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