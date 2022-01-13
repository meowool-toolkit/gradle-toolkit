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
 *
 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
@file:Suppress("SpellCheckingInspection")

import org.gradle.api.initialization.Settings
import java.io.File
import java.nio.file.Path

/**
 * Recursively import all projects that contain `build.gradle` or `build.gradle.kts` in the [includeDir].
 *
 * Please note that for the directories you don’t want to import, you can add a file named `.skip-import`, which
 * will skip import.
 *
 * @param includeDir The directory of project included.
 * @param excludeBuildDir Will not import all subdirectories in the 'build' name directory. But if you want to import
 *   some build directory, you can add a file named `.force-import` to overwrite this behavior.
 * @param excludeBy Exclude files if the given condition is true.
 *
 * @see Settings.include
 */
fun Settings.importProjects(
  includeDir: File,
  excludeBuildDir: Boolean = true,
  excludeBy: (File) -> Boolean,
) = includeDir.walkTopDown()
  .onEnter {
    if (it == includeDir) return@onEnter true
    if (it.startsWith(rootDir.resolve("buildSrc"))) return@onEnter false
    if (it.resolve(".force-import").exists().not()) {
      if (excludeBuildDir && it.name == "build") return@onEnter false
    }
    it.resolve(".skip-import").exists().not() && excludeBy(it).not()
  }
  .filter {
    it.isDirectory && it != rootDir &&
      it.resolve("build.gradle.kts").exists() ||
      it.resolve("build.gradle").exists() ||
      it.resolve("settings.gradle.kts").exists() ||
      it.resolve("settings.gradle").exists()
  }
  .forEach { include(":${it.relativeTo(rootDir).path.replace(File.separatorChar, ':')}") }

/**
 * Recursively import all projects that contain `build.gradle` or `build.gradle.kts` in the [includeDirPath].
 *
 * Please note that for the directories you don’t want to import, you can add a file named `.skip-import`, which
 * will skip import.
 *
 * @param includeDirPath The directory of project included.
 * @param excludeBy Exclude files if the given condition is true.
 *
 * @see Settings.include
 */
fun Settings.importProjects(
  includeDirPath: String,
  excludeBuildDir: Boolean = true,
  excludeBy: (File) -> Boolean,
) = this.importProjects(File(includeDirPath), excludeBuildDir, excludeBy)

/**
 * Recursively import all projects that contain `build.gradle` or `build.gradle.kts` in the [includeDirPath].
 *
 * Please note that for the directories you don’t want to import, you can add a file named `.skip-import`, which
 * will skip import.
 *
 * @param includeDirPath The directory of project included.
 * @param excludeBy Exclude files if the given condition is true.
 *
 * @see Settings.include
 */
fun Settings.importProjects(
  includeDirPath: Path,
  excludeBuildDir: Boolean = true,
  excludeBy: (File) -> Boolean,
) = this.importProjects(includeDirPath.toFile(), excludeBuildDir, excludeBy)

/**
 * Recursively import all projects that contain `build.gradle` or `build.gradle.kts` in the [includeDir].
 *
 * Please note that for the directories you don’t want to import, you can add a file named `.skip-import`, which
 * will skip import.
 *
 * @param includeDir The directory of project included.
 * @param excludeDirs The directories of project excluded.
 *
 * @see Settings.include
 */
fun Settings.importProjects(
  includeDir: File,
  excludeBuildDir: Boolean = true,
  vararg excludeDirs: File,
) = importProjects(includeDir, excludeBuildDir) { file ->
  excludeDirs.any { exclude -> file.startsWith(exclude) }
}

/**
 * Recursively import all projects that contain `build.gradle` or `build.gradle.kts` in the [includeDirPath].
 *
 * Please note that for the directories you don’t want to import, you can add a file named `.skip-import`, which
 * will skip import.
 *
 * @param includeDirPath The directory of project included.
 * @param excludeDirPaths The directories of project excluded.
 *
 * @see Settings.include
 */
fun Settings.importProjects(
  includeDirPath: String,
  excludeBuildDir: Boolean = true,
  vararg excludeDirPaths: String,
) = this.importProjects(File(includeDirPath), excludeBuildDir, *excludeDirPaths.map(::File).toTypedArray())

/**
 * Recursively import all projects that contain `build.gradle` or `build.gradle.kts` in the [includeDirPath].
 *
 * Please note that for the directories you don’t want to import, you can add a file named `.skip-import`, which
 * will skip import.
 *
 * @param includeDirPath The directory of project included.
 * @param excludeDirPaths The directories of project excluded.
 *
 * @see Settings.include
 */
fun Settings.importProjects(
  includeDirPath: Path,
  excludeBuildDir: Boolean = true,
  vararg excludeDirPaths: Path,
) = this.importProjects(includeDirPath.toFile(), excludeBuildDir, *excludeDirPaths.map { it.toFile() }.toTypedArray())
