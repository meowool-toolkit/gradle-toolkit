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
package com.meowool.gradle.toolkit.internal

import com.meowool.gradle.toolkit.DependencyMapperExtension
import kotlinx.serialization.Serializable
import org.gradle.api.Project
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Internal shared APIs.
 *
 * @author 凛 (RinOrz)
 */
@InternalGradleToolkitApi
object DependencyMapperInternal {
  const val CacheDir = ".dependency-mapper"
  const val CacheJson = "cache.json"
  const val CacheJarsDir = "jars"

  /**
   * Applies the [block] and start mapping dependencies.
   */
  fun mapping(project: Project, block: DependencyMapperExtension.() -> Unit): Boolean =
    DependencyMapperExtensionImpl(project).apply(block).mapping()

  /**
   * Collects the dependencies defined in the [block] into [destination].
   *
   * @see DependencyOutputList
   */
  fun collectDependencies(project: Project, destination: File, block: DependencyMapperExtension.() -> Unit) =
    DependencyMapperExtensionImpl(project).apply(block).collectDependencies(destination)

  /**
   * Relying on the output list, only contains dependency information.
   */
  @Serializable
  @InternalGradleToolkitApi
  data class DependencyOutputList(
    val libraries: MutableList<String> = mutableListOf(),
    val plugins: MutableList<String> = mutableListOf(),
    val mappedLibraries: MutableMap<String, String> = ConcurrentHashMap(),
    val mappedPlugins: MutableMap<String, String> = ConcurrentHashMap(),
  ) {
    val size: Int get() = libraries.size + plugins.size + mappedLibraries.size + mappedPlugins.size
  }
}
