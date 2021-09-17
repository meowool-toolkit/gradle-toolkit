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
package org.gradle.kotlin.dsl

import de.fayard.refreshVersions.RefreshVersionsExtension
import de.fayard.refreshVersions.core.extensions.gradle.isBuildSrc
import org.gradle.api.initialization.Settings

inline fun Settings.refreshVersions(configure: RefreshVersionsExtension.() -> Unit) {
  // This function is needed because Gradle doesn't generate accessors for
  // settings extensions.
  require(isBuildSrc.not()) {
    "Configuring refreshVersions in buildSrc is not supported, " +
      "please configure it in the root project. " +
      "If you have a use case for a separate config in buildSrc, " +
      "please open an issue about that on GitHub."
  }
  extensions.getByType<RefreshVersionsExtension>().configure()
}
