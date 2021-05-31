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
 */
package de.fayard.refreshVersions

import de.fayard.refreshVersions.core.internal.cli.AnsiColor
import de.fayard.refreshVersions.internal.promptProjectSelection
import de.fayard.refreshVersions.internal.runInteractiveMigrationToDependenciesConstants
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.logging.configuration.ConsoleOutput
import org.gradle.api.tasks.TaskAction

open class RefreshVersionsDependenciesMigrationTask : DefaultTask() {

  @TaskAction
  fun taskActionMigrate() {
    check(project == project.rootProject) { "This task is designed to run on root project only." }
    require(project.gradle.startParameter.consoleOutput == ConsoleOutput.Plain) {
      "Please, run the task with the option " +
        AnsiColor.MAGENTA.background +
        AnsiColor.WHITE.boldHighIntensity +
        "--console=plain" +
        AnsiColor.RESET
    }
    while (Thread.currentThread().isInterrupted.not()) {
      val selectedProject = promptProjectSelection(project)
        ?: return
      runBlocking {
        runInteractiveMigrationToDependenciesConstants(selectedProject)
      }
    }
  }
}
