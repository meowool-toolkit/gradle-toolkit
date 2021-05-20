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
package de.fayard.refreshVersions.core.internal

import de.fayard.refreshVersions.core.DependencyVersionsFetcher
import de.fayard.refreshVersions.core.ModuleId
import de.fayard.refreshVersions.core.Version
import java.text.SimpleDateFormat
import java.util.*

internal abstract class MavenDependencyVersionsFetcher(
  moduleId: ModuleId,
  repoUrl: String
) : DependencyVersionsFetcher(
  moduleId = moduleId,
  repoKey = repoUrl
) {
  protected abstract suspend fun getXmlMetadataOrNull(): String?

  override suspend fun getAvailableVersionsOrNull(versionFilter: ((Version) -> Boolean)?): SuccessfulResult? {

    val xml = getXmlMetadataOrNull() ?: return null

    val allVersions = parseVersionsFromMavenMetaData(xml)
    return SuccessfulResult(
      lastUpdateTimestampMillis = parseLastUpdatedFromMavenMetaData(xml),
      availableVersions = if (versionFilter == null) {
        allVersions
      } else {
        allVersions.filter(versionFilter)
      }
    )
  }

  companion object {
    @Suppress("SpellCheckingInspection")
    private val mavenLastUpdatedDateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.ROOT).apply {
      timeZone = TimeZone.getTimeZone("UTC")
    }

    private fun parseLastUpdatedFromMavenMetaData(xml: String): Long = runCatching {
      val dateString = xml.substringAfter("<lastUpdated>").substringBefore("</lastUpdated>")
      return mavenLastUpdatedDateFormat.parse(dateString).time
    }.getOrDefault(0)

    private fun parseVersionsFromMavenMetaData(xml: String): List<Version> {
      return xml.substringAfter("<versions>").substringBefore("</versions>")
        .split("<version>", "</version>")
        .mapNotNull { if (it.isBlank()) null else Version(it.trim()) }
    }
  }
}
