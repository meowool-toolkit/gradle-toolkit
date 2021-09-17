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

import de.fayard.refreshVersions.core.internal.InternalRefreshVersionsApi

/**
 * Since a bug in refreshVersions can break people's builds,
 * we put risky changes and new features behind feature flags.
 *
 * If it starts disabled, it allows us to test it without affecting people that don't opt-in,
 * and in all cases, it allows people to disable it without downgrading if problems are caused.
 *
 * After several releases where the change didn't cause any issue, we might deprecate the flag,
 * ignore it, and remove the dead code.
 *
 * Users can temporarily control the flags from command-line as such:
 *
 * ```bash
 * ./gradle refreshVersions --enable LIBS
 * ./gradle refreshVersions --disable GRADLE_UPDATES
 * ```
 *
 * Or they can permanently control the flags from the Gradle Settings file:
 *
 * ```kotlin
 * refreshVersions {
 *      featureFlags {
 *          enable(LIBS)
 *          disable(GRADLE_UPDATES)
 *      }
 * }
 * ```
 */
enum class FeatureFlag(private val enabledByDefault: Boolean?) {

  // NEVER REMOVE A FLAG HERE since it would break projects using it on upgrade.
  // Instead, mark it as deprecated, like this: @Deprecated("your message here")

  GRADLE_UPDATES(enabledByDefault = true),
  LIBS(enabledByDefault = false)
  ;

  companion object {
    /**
     * Where we store the settings coming from the command-line or the Settings file
     */
    @InternalRefreshVersionsApi
    val userSettings: MutableMap<FeatureFlag, Boolean> = mutableMapOf()
  }

  /**
   * Whether the flag is enabled once the user settings are set
   * Intended usage:
   * `if (GRADLE_UPDATES.isEnabled) lookupAvailableGradleVersions() else emptyList()`
   */
  internal val isEnabled: Boolean
    get() = when (enabledByDefault) {
      false -> userSettings[this] == true
      true -> userSettings[this] != false
      null -> false
    }
}
