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
package de.fayard.refreshVersions.internal

import de.fayard.refreshVersions.core.countDependenciesWithHardcodedVersions
import de.fayard.refreshVersions.core.internal.RefreshVersionsConfigHolder
import de.fayard.refreshVersions.core.internal.cli.AnsiColor
import de.fayard.refreshVersions.core.internal.cli.CliGenericUi
import de.fayard.refreshVersions.core.shouldBeIgnored
import kotlinx.coroutines.isActive
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import kotlin.coroutines.coroutineContext

// TODO: Ignore the following dependency: org.jetbrains.kotlin:kotlin-android-extensions-runtime

internal fun promptProjectSelection(rootProject: Project): Project? {
  require(rootProject == rootProject.rootProject) { "Expected a rootProject but got $rootProject" }
  val versionsMap = RefreshVersionsConfigHolder.readVersionsMap()
  val projectsWithHardcodedDependenciesVersions: List<Pair<Project, Int>> = rootProject.allprojects.mapNotNull {
    val hardcodedDependenciesVersionsCount = it.countDependenciesWithHardcodedVersions(versionsMap)
    if (hardcodedDependenciesVersionsCount > 0) {
      it to hardcodedDependenciesVersionsCount
    } else null
  }
  val cliGenericUi = CliGenericUi()
  val index = cliGenericUi.showMenuAndGetIndexOfChoice(
    header = "All the following modules have hardcoded dependencies versions",
    footer = "Type the number of the Gradle module you want to migrate first:",
    numberedEntries = projectsWithHardcodedDependenciesVersions.map { (project, hardcodedVersionsCount) ->
      "${project.path} ($hardcodedVersionsCount)"
    } + "Exit"
  )
  return projectsWithHardcodedDependenciesVersions.getOrNull(index.value)?.first
}

internal suspend fun runInteractiveMigrationToDependenciesConstants(project: Project) {
  val versionsMap = RefreshVersionsConfigHolder.readVersionsMap()
  while (coroutineContext.isActive) {
    val selectedConfiguration = project.promptConfigurationSelection(versionsMap) ?: return
    runConfigurationDependenciesMigration(
      project,
      versionsMap,
      selectedConfiguration
    )
  }
}

private fun Project.promptConfigurationSelection(versionsMap: Map<String, String>): Configuration? {
  @Suppress("UnstableApiUsage")
  val versionKeyReader = RefreshVersionsConfigHolder.versionKeyReader
  val configurationsWithHardcodedDependenciesVersions = configurations.mapNotNull { configuration ->
    if (configuration.shouldBeIgnored()) return@mapNotNull null
    val count = configuration.countDependenciesWithHardcodedVersions(versionsMap, versionKeyReader)
    return@mapNotNull if (count == 0) null else configuration to count
  }
  val cliGenericUi = CliGenericUi()
  val index = cliGenericUi.showMenuAndGetIndexOfChoice(
    header = "${AnsiColor.WHITE.boldHighIntensity}${AnsiColor.GREEN.background}" +
      "You selected project $path" +
      "${AnsiColor.RESET}\n" +
      "The following configurations have dependencies with hardcoded versions",
    footer = "Type the number of the configuration you want to migrate first:",
    numberedEntries = configurationsWithHardcodedDependenciesVersions.map { (configuration, count) ->
      "${configuration.name} ($count)"
    } + "Back"
  )
  return configurationsWithHardcodedDependenciesVersions.getOrNull(index.value)?.first
}
