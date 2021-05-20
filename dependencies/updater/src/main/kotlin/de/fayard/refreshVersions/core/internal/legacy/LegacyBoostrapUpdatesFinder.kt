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
package de.fayard.refreshVersions.core.internal.legacy

import de.fayard.refreshVersions.core.ModuleId
import de.fayard.refreshVersions.core.RefreshVersionsCorePlugin
import de.fayard.refreshVersions.core.Version
import de.fayard.refreshVersions.core.internal.DependencyWithVersionCandidates
import de.fayard.refreshVersions.core.internal.RefreshVersionsConfigHolder
import de.fayard.refreshVersions.core.internal.VersionCandidatesResultMode
import de.fayard.refreshVersions.core.internal.getDependencyVersionFetchers
import de.fayard.refreshVersions.core.internal.getVersionCandidates
import okhttp3.OkHttpClient

internal object LegacyBoostrapUpdatesFinder {

  suspend fun getSelfUpdates(
    httpClient: OkHttpClient,
    resultMode: VersionCandidatesResultMode
  ): DependencyWithVersionCandidates {
    val moduleId = ModuleId(group = "de.fayard.refreshVersions", name = "refreshVersions")

    val versionsFetchers = RefreshVersionsConfigHolder.settings.getDependencyVersionFetchers(
      httpClient = httpClient,
      dependencyFilter = { dependency ->
        dependency.group == moduleId.group && dependency.name == moduleId.name
      }
    ).toList()

    val currentVersion = RefreshVersionsCorePlugin.currentVersion

    return DependencyWithVersionCandidates(
      moduleId = moduleId,
      currentVersion = currentVersion,
      versionsCandidates = versionsFetchers.getVersionCandidates(
        currentVersion = Version(currentVersion),
        resultMode = resultMode
      )
    )
  }
}
