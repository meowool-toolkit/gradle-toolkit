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

import de.fayard.refreshVersions.core.FeatureFlag
import org.gradle.api.Action
import org.gradle.api.Incubating
import java.io.File

open class RefreshVersionsExtension {

  var versionsPropertiesFile: File? = null
  var extraArtifactVersionKeyRules: List<String> = emptyList()
  internal var isBuildSrcLibsEnabled = false

  @Incubating
  fun enableBuildSrcLibs() {
    isBuildSrcLibsEnabled = true
  }

  fun extraArtifactVersionKeyRules(file: File) {
    extraArtifactVersionKeyRules = extraArtifactVersionKeyRules + file.readText()
  }

  fun extraArtifactVersionKeyRules(rawRules: String) {
    extraArtifactVersionKeyRules = extraArtifactVersionKeyRules + rawRules
  }

  fun featureFlags(extension: Action<FeatureFlagExtension>) {
    extension.execute(FeatureFlagExtension())
  }
}

open class FeatureFlagExtension {
  fun enable(flag: FeatureFlag) {
    FeatureFlag.userSettings[flag] = true
  }
  fun disable(flag: FeatureFlag) {
    FeatureFlag.userSettings[flag] = false
  }
}
