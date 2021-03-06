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
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
package com.meowool.gradle.toolkit

import kotlinx.serialization.Serializable

/**
 * Represents a plugin dependency meta-information.
 *
 * @author 凛 (RinOrz)
 */
@Serializable
class PluginId internal constructor(private val id: CharSequence) : CharSequence by id {

  /**
   * Converts this plugin id to library dependency.
   *
   * The gradle find the coordinates by the following marker:
   * `pluginId:pluginId.gradle.plugin:version`
   *
   * For more details, see (Plugin Marker Artifacts)[https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_markers]
   */
  fun toLibraryDependency(): LibraryDependency = LibraryDependency("$id:$id.gradle.plugin")

  override fun hashCode(): Int = toString().hashCode()
  override fun equals(other: Any?): Boolean = toString() == other?.toString()
  override fun toString(): String = id.toString()
}
