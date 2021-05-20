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
@file:Suppress("UNREACHABLE_CODE")

// Remove when impl complete

package de.fayard.refreshVersions.core.internal

@InternalRefreshVersionsApi
abstract class ArtifactVersionKeyReader private constructor() {

  abstract fun readVersionKey(group: String, name: String): String?

  operator fun plus(other: ArtifactVersionKeyReader): ArtifactVersionKeyReader {
    val initial = this
    return object : ArtifactVersionKeyReader() {
      override fun readVersionKey(group: String, name: String): String? {
        return other.readVersionKey(group, name) ?: initial.readVersionKey(group, name)
      }
    }
  }

  companion object {

    fun fromRules(fileContent: String): ArtifactVersionKeyReader = fromRules(listOf(fileContent))

    fun fromRules(filesContent: List<String>): ArtifactVersionKeyReader {
      val rules = filesContent.flatMap { parseArtifactVersionKeysRules(it) }.sortedDescending()
      return object : ArtifactVersionKeyReader() {
        override fun readVersionKey(group: String, name: String): String? {
          return rules.firstOrNull { it.matches(group, name) }?.key(group, name)
        }
      }
    }
  }
}

internal fun parseArtifactVersionKeysRules(fileContent: String): List<ArtifactVersionKeyRule> {
  val lines = fileContent.lineSequence()
    .map {
      val indexOfLineComment = it.indexOf("//")
      if (indexOfLineComment == -1) it else it.substring(startIndex = 0, endIndex = indexOfLineComment)
    }
    .filter { it.isNotBlank() }
    .map { it.trimEnd() }
    .toList()
  require(lines.size % 2 == 0) {
    "Every artifact version key rule is made of two lines, but an odd count of rules lines has been found."
  }
  return MutableList(lines.size / 2) { i ->
    ArtifactVersionKeyRule(
      artifactPattern = lines[i * 2],
      versionKeyPattern = lines[i * 2 + 1]
    )
  }
}
