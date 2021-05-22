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

import de.fayard.refreshVersions.core.extensions.gradle.isGradlePlugin
import de.fayard.refreshVersions.core.extensions.gradle.moduleIdentifier
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalDependency

@InternalRefreshVersionsApi
fun Dependency.hasHardcodedVersion(
  versionMap: Map<String, String>,
  versionKeyReader: ArtifactVersionKeyReader
): Boolean = isManageableVersion(versionMap, versionKeyReader).not()

@InternalRefreshVersionsApi
fun Dependency.isManageableVersion(
  versionMap: Map<String, String>,
  versionKeyReader: ArtifactVersionKeyReader
): Boolean {
  return when {
    this is ExternalDependency && versionPlaceholder in this.versionConstraint.rejectedVersions -> true
    version == versionPlaceholder -> true
    moduleIdentifier?.isGradlePlugin == true -> {
      val versionFromProperty =
        versionMap[getVersionPropertyName(moduleIdentifier!!, versionKeyReader)]
          ?: return false
      versionFromProperty.isAVersionAlias().not()
    }
    else -> false
  }
}