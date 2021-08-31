import com.meowool.sweekt.removeBlanks
import com.meowool.sweekt.substringAfter
import com.meowool.sweekt.substringBefore
import org.gradle.api.Project
import java.io.File

/**
 * @author 凛 (https://github.com/RinOrz)
 */
internal class GradleScriptModifier(val project: Project) {

  fun insertClasspathIfNotFound(classpath: String) {
    // Insert to existing `buildscript` block
    findBuildscriptBlock()?.apply {
      val source = readText()
      if (source.contains(classpath).not()) {
        writeText(insertClasspath(source, classpath))
      }
    } ?:
    // Create new `buildscript` block
    when {
      rootSettingsFile != null -> rootSettingsFile!!.addBuildscript(classpath)
      rootBuildFile != null -> rootBuildFile!!.addBuildscript(classpath)
      else -> rootProject.file(SettingsKts).apply { createNewFile() }.addBuildscript(classpath)
    }
  }

  private val rootProject get() = project.rootProject
  private val rootBuildFile get() = existingRootFile(Build) ?: existingRootFile(BuildKts)
  private val rootSettingsFile get() = existingRootFile(SettingsKts) ?: existingRootFile(Settings)

  private fun existingRootFile(path: String): File? = rootProject.file(path).takeIf { it.exists() }

  /** Find the file where `buildscript` block exists. */
  private fun findBuildscriptBlock() = when {
    rootSettingsFile.hasBuildscriptBlock() -> rootSettingsFile
    rootBuildFile.hasBuildscriptBlock() -> rootBuildFile
    else -> null
  }

  private fun File?.addBuildscript(classpath: String) = this?.writeText(createBuildscriptBlock(readText(), classpath))

  companion object {
    private const val BuildscriptBlock = "buildscript"
    const val Build = "build.gradle"
    const val BuildKts = "$Build.kts"
    const val Settings = "settings.gradle"
    const val SettingsKts = "$Settings.kts"

    /** buildscript { dependencies.classpath(..) } */
    fun createBuildscriptBlock(source: String, classpathLine: String): String = buildString {
      append("$BuildscriptBlock { ")
      append("dependencies.")
      append(classpathLine)
      appendLine(" }\n")
      append(source)
    }

    fun insertClasspath(source: String, classpathLine: String): String {
      // buildscript { .. }
      return when (source.hasBuildscriptBlock()) {
        false -> createBuildscriptBlock(source, classpathLine)
        // Append new line to after `buildscript {`
        else -> {
          var index = source.indexOf(BuildscriptBlock)
          for (char in source.substringAfter(index)) {
            index++
            // n spaces: `buildscript   {`
            if (char == '{') {
              return buildString {
                appendLine(source.substringBefore(index + 1))
                appendLine("!! Please manually adjust the indentation or position here, and then remove these warnings // this is the classpath generated by the meowool-dependencies-mapper")
                appendLine("!! For more details: https://github.com/meowool-toolkit/gradle-toolkit/tree/master/dependencies/mapper")
                append("dependencies.")
                appendLine(classpathLine)
                append(source.substringAfter(index))
              }
            }
          }
          error("Unable to insert `classpath`")
        }
      }
    }

    fun String.hasBuildscriptBlock() = this.removeLineBreaks().removeBlanks().contains("$BuildscriptBlock{")
    private fun File?.hasBuildscriptBlock() = this?.readText()?.hasBuildscriptBlock() == true
  }
}