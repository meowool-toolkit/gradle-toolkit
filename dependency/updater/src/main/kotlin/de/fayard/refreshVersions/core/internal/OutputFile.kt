/*
 * Copyright (c) 2021. The Meowool Organization Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
package de.fayard.refreshVersions.core.internal

import org.gradle.api.Project
import java.io.File

@InternalRefreshVersionsApi
enum class OutputFile(var path: String, var existed: Boolean = false, val alternativePath: String? = null) {
  OUTPUT_DIR("buildSrc/src/main/kotlin"),
  BUILD("buildSrc/build.gradle.kts", alternativePath = "buildSrc/build.gradle"),
  GIT_IGNORE("buildSrc/.gitignore"),
  LIBS("buildSrc/src/main/kotlin/Libs.kt"),
  VERSIONS_KT("buildSrc/src/main/kotlin/Versions.kt"),
  VERSIONS_PROPERTIES("versions.properties"),
  SETTINGS_GRADLE("settings.gradle"),
  SETTINGS_GRADLE_KTS("settings.gradle.kts"),
  GRADLE_VERSIONS_CATALOG("gradle/libs.versions.toml"),
  ;

  fun readText(project: Project) = when {
    project.file(path).canRead() -> project.file(path).readText()
    alternativePath != null && project.file(alternativePath).canRead() -> project.file(alternativePath).readText()
    else -> {
      println("${ANSI_RED}Cannot read file $path ${alternativePath ?: ""} $ANSI_RESET")
      error("File not found $this")
    }
  }

  fun writeText(text: String, project: Project, mustExists: Boolean = false) = when {
    !mustExists -> project.file(path).writeText(text)
    project.file(path).exists() -> project.file(path).writeText(text)
    alternativePath != null && project.file(alternativePath).canRead() -> project.file(alternativePath)
      .writeText(text)
    else -> {
      println("${ANSI_RED}Cannot write file $path ${alternativePath ?: ""} $ANSI_RESET")
      error("File not found $this")
    }
  }

  fun logFileWasModified(delete: Boolean = false) {
    logFileWasModified(path, existed, delete)
  }

  companion object {
    // COLORS
    private const val ANSI_RESET = "\u001B[0m"
    private const val ANSI_GREEN = "\u001B[32m"
    private const val ANSI_RED = "\u001B[31m"
    private const val ANSI_BLUE = "\u001B[34m"

    fun logFileWasModified(path: String, existed: Boolean, delete: Boolean = false) {
      val color = when {
        existed -> ANSI_BLUE
        delete -> ANSI_RED
        else -> ANSI_GREEN
      }
      val status = when {
        delete -> "        deleted:    "
        existed -> "        modified:   "
        else -> "        new file:   "
      }
      println("$color$status$path$ANSI_RESET")
    }

    fun checkWhichFilesExist(rootDir: File) {
      values().forEach { outputFile ->
        outputFile.existed = when {
          rootDir.resolve(outputFile.path).exists() -> true
          outputFile.alternativePath == null -> false
          else -> rootDir.resolve(outputFile.alternativePath).exists()
        }
      }
    }
  }
}
