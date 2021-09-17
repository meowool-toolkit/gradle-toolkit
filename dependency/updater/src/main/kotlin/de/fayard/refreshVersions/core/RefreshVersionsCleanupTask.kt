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
 * 除如果您正在修改此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
package de.fayard.refreshVersions.core

import de.fayard.refreshVersions.core.internal.RefreshVersionsConfigHolder
import de.fayard.refreshVersions.core.internal.SettingsPluginsUpdater.removeCommentsAddedByUs
import de.fayard.refreshVersions.core.internal.versions.VersionsPropertiesModel
import de.fayard.refreshVersions.core.internal.versions.VersionsPropertiesModel.Section
import de.fayard.refreshVersions.core.internal.versions.readFrom
import de.fayard.refreshVersions.core.internal.versions.writeTo
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class RefreshVersionsCleanupTask : DefaultTask() {

  @TaskAction
  fun cleanUpVersionsProperties() {
    val model = VersionsPropertiesModel.readFrom(RefreshVersionsConfigHolder.versionsPropertiesFile)

    val sectionsWithoutAvailableUpdates = model.sections.map { section ->
      when (section) {
        is Section.Comment -> section
        is Section.VersionEntry -> section.copy(availableUpdates = emptyList())
      }
    }
    val newModel = model.copy(sections = sectionsWithoutAvailableUpdates)
    newModel.writeTo(RefreshVersionsConfigHolder.versionsPropertiesFile)
  }

  @TaskAction
  fun cleanUpSettings() {
    val settingsFiles = listOf(
      "settings.gradle",
      "settings.gradle.kts",
      "buildSrc/settings.gradle",
      "buildSrc/settings.gradle.kts"
    ).mapNotNull { path ->
      project.file(path).takeIf { it.exists() }
    }

    settingsFiles.forEach { settingsFile ->
      val initialContent = settingsFile.readText()
      val newContent = buildString {
        append(initialContent)
        removeCommentsAddedByUs()
      }
      if (initialContent.length != newContent.length) {
        settingsFile.writeText(newContent)
      }
    }
  }
}
