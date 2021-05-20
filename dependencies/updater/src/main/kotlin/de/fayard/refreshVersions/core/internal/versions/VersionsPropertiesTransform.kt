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
package de.fayard.refreshVersions.core.internal.versions

import de.fayard.refreshVersions.core.internal.versions.VersionsPropertiesModel.Section
import de.fayard.refreshVersions.core.internal.versions.VersionsPropertiesModel.Section.VersionEntry

internal operator fun VersionsPropertiesModel.plus(
  newEntry: VersionEntry
): VersionsPropertiesModel {
  require(newEntry.leadingCommentLines.isEmpty())
  require(newEntry.trailingCommentLines.isEmpty())

  return this.copy(
    sections = mutableListOf<Section>().also { newList ->
      newList.addAll(sections)

      val existingEntryWithSameKey: VersionEntry?
      val targetIndex = run {
        for (i in sections.indices) {
          val element = sections[i]
          if (element !is VersionEntry || element.key < newEntry.key) continue

          existingEntryWithSameKey = if (element.key == newEntry.key) element else null
          return@run i
        }
        existingEntryWithSameKey = null
        sections.size
      }

      when (existingEntryWithSameKey) {
        null -> newList.add(index = targetIndex, element = newEntry)
        else -> newList[targetIndex] = existingEntryWithSameKey.copy(
          availableUpdates = newEntry.availableUpdates
        )
      }
    }
  )
}
