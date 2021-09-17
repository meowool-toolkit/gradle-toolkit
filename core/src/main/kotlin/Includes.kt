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
@file:Suppress("SpellCheckingInspection")

import org.gradle.api.initialization.Settings
import java.io.File
import java.nio.file.Path

/**
 * Recursively import all projects that contain `build.gradle` or `build.gradle.kts` in the [includeDir].
 *
 * Please note that for the directories you don’t want to import, you can add a file named `.skipimport`, which
 * will skip import.
 *
 * @param includeDir the directory of project included.
 * @param excludeBy exclude files if the given condition is true.
 *
 * @see Settings.include
 */
fun Settings.importProjects(
  includeDir: File,
  excludeBy: (File) -> Boolean,
) = includeDir.walkTopDown()
  .onEnter {
    if (it == includeDir) return@onEnter true
    it.resolve(".skipimport").exists().not() &&
      excludeBy(it).not() &&
      // Enter the dir containing build scripts
      it.containsBuildScript() &&
      // Enter a non-build dir, or the parent dir does not contain build scripts
      (it.name != "build" || it.parentFile.containsBuildScript().not())
  }
  .filter { it.isDirectory && it != rootDir }
  .forEach { include(":${it.relativeTo(rootDir).path.replace(File.separatorChar, ':')}") }

/**
 * Recursively import all projects that contain `build.gradle` or `build.gradle.kts` in the [includeDirPath].
 *
 * Please note that for the directories you don’t want to import, you can add a file named `.skipimport`, which
 * will skip import.
 *
 * @param includeDirPath the directory of project included.
 * @param excludeBy exclude files if the given condition is true.
 *
 * @see Settings.include
 */
fun Settings.importProjects(
  includeDirPath: String,
  excludeBy: (File) -> Boolean,
) = this.importProjects(File(includeDirPath), excludeBy)

/**
 * Recursively import all projects that contain `build.gradle` or `build.gradle.kts` in the [includeDirPath].
 *
 * Please note that for the directories you don’t want to import, you can add a file named `.skipimport`, which
 * will skip import.
 *
 * @param includeDirPath the directory of project included.
 * @param excludeBy exclude files if the given condition is true.
 *
 * @see Settings.include
 */
fun Settings.importProjects(
  includeDirPath: Path,
  excludeBy: (File) -> Boolean,
) = this.importProjects(includeDirPath.toFile(), excludeBy)

/**
 * Recursively import all projects that contain `build.gradle` or `build.gradle.kts` in the [includeDir].
 *
 * Please note that for the directories you don’t want to import, you can add a file named `.skipimport`, which
 * will skip import.
 *
 * @param includeDir the directory of project included.
 * @param excludeDirs the directories of project excluded.
 *
 * @see Settings.include
 */
fun Settings.importProjects(
  includeDir: File,
  vararg excludeDirs: File,
) = importProjects(includeDir) { file ->
  excludeDirs.any { exclude -> file.startsWith(exclude) }
}

/**
 * Recursively import all projects that contain `build.gradle` or `build.gradle.kts` in the [includeDirPath].
 *
 * Please note that for the directories you don’t want to import, you can add a file named `.skipimport`, which
 * will skip import.
 *
 * @param includeDirPath the directory of project included.
 * @param excludeDirPaths the directories of project excluded.
 *
 * @see Settings.include
 */
fun Settings.importProjects(
  includeDirPath: String,
  vararg excludeDirPaths: String,
) = this.importProjects(File(includeDirPath), *excludeDirPaths.map(::File).toTypedArray())

/**
 * Recursively import all projects that contain `build.gradle` or `build.gradle.kts` in the [includeDirPath].
 *
 * Please note that for the directories you don’t want to import, you can add a file named `.skipimport`, which
 * will skip import.
 *
 * @param includeDirPath the directory of project included.
 * @param excludeDirPaths the directories of project excluded.
 *
 * @see Settings.include
 */
fun Settings.importProjects(
  includeDirPath: Path,
  vararg excludeDirPaths: Path,
) = this.importProjects(includeDirPath.toFile(), *excludeDirPaths.map { it.toFile() }.toTypedArray())

private fun File?.containsBuildScript(): Boolean {
  if (this == null) return false
  return resolve("build.gradle.kts").exists() || resolve("build.gradle").exists()
}
