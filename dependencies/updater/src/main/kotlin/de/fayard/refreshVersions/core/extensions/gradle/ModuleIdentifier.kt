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
package de.fayard.refreshVersions.core.extensions.gradle

import de.fayard.refreshVersions.core.ModuleId
import org.gradle.api.artifacts.ModuleIdentifier

internal val ModuleIdentifier.isGradlePlugin: Boolean
  get() = name.endsWith(".gradle.plugin")

@Suppress("nothing_to_inline")
internal inline fun ModuleIdentifier.toModuleId() = ModuleId(group = group, name = name)

internal fun ModuleId.toModuleIdentifier(): ModuleIdentifier = object : ModuleIdentifier {
  override fun getGroup(): String = this@toModuleIdentifier.group!!
  override fun getName(): String = this@toModuleIdentifier.name
}
