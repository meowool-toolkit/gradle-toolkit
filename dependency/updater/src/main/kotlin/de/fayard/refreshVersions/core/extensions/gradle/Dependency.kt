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
package de.fayard.refreshVersions.core.extensions.gradle

import de.fayard.refreshVersions.core.ModuleId
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleIdentifier

internal val Dependency.moduleId: ModuleId get() = ModuleId(group, name)

internal val Dependency.moduleIdentifier: ModuleIdentifier?
  get() {
    val group = group ?: return null
    val name = name
    return object : ModuleIdentifier {
      override fun getGroup(): String = group
      override fun getName(): String = name
      override fun toString(): String = "${getGroup()}:${getName()}"
    }
  }

internal val Dependency.isGradlePlugin: Boolean
  get() = name.endsWith(".gradle.plugin")
