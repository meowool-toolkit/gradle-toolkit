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
package com.meowool.gradle.toolkit

import com.meowool.sweekt.iteration.toArray

/**
 * Used to declare how to map library dependencies.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
interface LibraryDependencyDeclaration {

  /**
   * Transfers the coordinates of all gradle plugin ids to [target] declaration.
   *
   * Any dependency coordinates that meet the following marks will be considered as plugins:
   * `pluginId:pluginId.gradle.plugin:version`
   *
   * For more details, see (Plugin Marker Artifacts)[https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_markers]
   */
  fun transferPluginIds(target: PluginDependencyDeclaration)

  /**
   * Transfers the coordinates of all gradle plugin ids to target declaration.
   *
   * Any dependency coordinates that meet the following marks will be considered as plugins:
   * `pluginId:pluginId.gradle.plugin:version`
   *
   * For more details, see (Plugin Marker Artifacts)[https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_markers]
   *
   * @param targetDeclarationName The root class name of the target, see [DependencyMapperExtension.plugins]
   */
  fun transferPluginIds(targetDeclarationName: String = PluginDependencyDeclaration.DefaultRootClassName)

  /**
   * Adds the given [dependencies] to map as path.
   *
   * For example:
   * ```
   * map(
   *   "androidx.compose.ui:ui",
   *   "androidx.appcompat:appcompat",
   *   "androidx.activity:activity-compose",
   * )
   * ```
   */
  fun map(vararg dependencies: CharSequence)

  /**
   * Adds the given [dependencies] to map as path.
   *
   * For example:
   * ```
   * map(
   *   "androidx.compose.ui:ui",
   *   "androidx.appcompat:appcompat",
   *   "androidx.activity:activity-compose",
   * )
   * ```
   */
  fun map(dependencies: Iterable<CharSequence>) = map(*dependencies.toArray())

  /**
   * Adds the given pair to map the dependencies to the specified mapped paths.
   *
   * For example:
   * ```
   * map(
   *   "androidx.compose.ui:ui" to "Compose.Ui",
   *   "androidx.appcompat:appcompat" to "Appcompat.Core",
   * )
   * ```
   *
   * @param dependenciesAndPaths The first parameter of pairs is dependencies, the second parameter is mapped paths.
   */
  fun map(vararg dependenciesAndPaths: Pair<CharSequence, CharSequence>)

  /**
   * Adds the given pair to map the dependencies to the specified mapped paths.
   *
   * For example:
   * ```
   * map(
   *   "androidx.compose.ui:ui" to "Compose.Ui",
   *   "androidx.appcompat:appcompat" to "Appcompat.Core",
   * )
   * ```
   *
   * @param dependenciesAndPaths The first parameter of pairs is dependencies, the second parameter is mapped paths.
   */
  fun map(dependenciesAndPaths: Map<CharSequence, CharSequence>) = map(*dependenciesAndPaths.toList().toArray())

  /**
   * Adds a request to searches remote dependencies by [keywords].
   *
   * @param keywords The keywords of remote dependencies to be mapped.
   * @param declaration Declare how to search for dependencies.
   */
  fun search(vararg keywords: CharSequence, declaration: SearchDeclaration<LibraryDependency>.() -> Unit = {})

  /**
   * Adds a request to searches remote dependencies by [keywords].
   *
   * @param keywords The keywords of remote dependencies to be mapped.
   * @param declaration Declare how to search for dependencies.
   */
  fun search(keywords: Iterable<CharSequence>, declaration: SearchDeclaration<LibraryDependency>.() -> Unit = {}) =
    search(*keywords.toArray(), declaration = declaration)

  /**
   * Adds a request to searches for remote dependencies belonging to specified [groups].
   *
   * @param groups The groups of remote dependencies to be mapped.
   * @param declaration Declare how to search for dependencies.
   */
  fun searchGroups(vararg groups: CharSequence, declaration: SearchDeclaration<LibraryDependency>.() -> Unit = {})

  /**
   * Adds a request to searches for remote dependencies belonging to specified [groups].
   *
   * @param groups The groups of remote dependencies to be mapped.
   * @param declaration Declare how to search for dependencies.
   */
  fun searchGroups(groups: Iterable<CharSequence>, declaration: SearchDeclaration<LibraryDependency>.() -> Unit = {}) =
    searchGroups(*groups.toArray(), declaration = declaration)

  /**
   * Adds a request to searches for remote dependencies where the specified [prefixes] exist.
   *
   * @param prefixes The prefixes of remote dependencies to be mapped.
   * @param declaration Declare how to search for dependencies.
   */
  fun searchPrefixes(vararg prefixes: CharSequence, declaration: SearchDeclaration<LibraryDependency>.() -> Unit = {})

  /**
   * Adds a request to searches for remote dependencies where the specified [prefixes] exist.
   *
   * @param prefixes The prefixes of remote dependencies to be mapped.
   * @param declaration Declare how to search for dependencies.
   */
  fun searchPrefixes(
    prefixes: Iterable<CharSequence>,
    declaration: SearchDeclaration<LibraryDependency>.() -> Unit = {},
  ) = searchPrefixes(*prefixes.toArray(), declaration = declaration)

  /**
   * Adds a default search options block.
   * All search requests will use the specified [declaration] by default.
   */
  fun searchDefaultOptions(declaration: SearchDeclaration<LibraryDependency>.() -> Unit)

  /**
   * If the [predicate] is `true`, the corresponding dependency will be mapped, otherwise it will not be mapped.
   */
  fun filter(predicate: LibraryDependency.() -> Boolean)

  /**
   * If the [predicate] is `false`, the corresponding dependency will be mapped, otherwise it will not be mapped.
   */
  fun filterNot(predicate: LibraryDependency.() -> Boolean) = filter { predicate().not() }

  companion object {
    const val DefaultRootClassName = "Libs"
  }
}
