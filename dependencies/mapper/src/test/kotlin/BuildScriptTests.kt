import GradleScriptModifier.Companion.Build
import GradleScriptModifier.Companion.BuildKts
import GradleScriptModifier.Companion.Settings
import GradleScriptModifier.Companion.SettingsKts
import GradleScriptModifier.Companion.hasBuildscriptBlock
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import java.io.File

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class BuildScriptTests : FreeSpec({
  val classpath = "classpath(files(\"_\"))"
  val noBuildscriptBlock = """
    pluginManagement {
      repositories {
        gradlePluginPortal()
      }
    }
  """.trimIndent()
  val existingBuildscriptBlock = """
    buildscript { }
    include(":core")
  """.trimIndent()
  val newFileWithBuildscriptBlock = "buildscript { dependencies.$classpath }\n\n$noBuildscriptBlock"

  "has buildscript block" {
    noBuildscriptBlock.hasBuildscriptBlock().shouldBeFalse()
    existingBuildscriptBlock.hasBuildscriptBlock().shouldBeTrue()
  }

  "add buildscript block" {
    GradleScriptModifier.createBuildscriptBlock(noBuildscriptBlock, classpath) shouldBe newFileWithBuildscriptBlock
  }

  "project build script file" - {
    fun createProject() = ProjectBuilder.builder().withProjectDir(tempdir()).build()
    fun insert(block: Project.() -> File) {
      val project = createProject()
      val modifier = GradleScriptModifier(project)
      val file = project.block()
      file.writeText(noBuildscriptBlock)
      modifier.insertClasspathIfNotFound(classpath)
      file.readText() shouldBe newFileWithBuildscriptBlock
    }
    "insert to $Build" {
      insert { file(Build) }
    }
    "insert to $BuildKts" {
      insert { file(BuildKts) }
    }
    "insert to $Settings" {
      insert { file(Settings) }
    }
    "insert to $SettingsKts" {
      insert { file(SettingsKts) }
    }
    "classpath" {
      val project = createProject()
      project.file("test.file").relativeTo(base = project.rootDir).normalize().path shouldBe "test.file"
    }
  }
})