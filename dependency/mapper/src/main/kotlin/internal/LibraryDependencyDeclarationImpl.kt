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
package com.meowool.gradle.toolkit.internal

import com.meowool.gradle.toolkit.DependencyFormatter
import com.meowool.gradle.toolkit.LibraryDependency
import com.meowool.gradle.toolkit.LibraryDependencyDeclaration
import com.meowool.gradle.toolkit.PluginDependencyDeclaration
import com.meowool.gradle.toolkit.SearchDeclaration
import com.meowool.gradle.toolkit.internal.client.DependencyRepositoryClient
import com.meowool.sweekt.String
import com.meowool.sweekt.removeBlanks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * @author 凛 (https://github.com/RinOrz)
 */
@Serializable
internal class LibraryDependencyDeclarationImpl(
  override val rootClassName: String,
) : LibraryDependencyDeclaration, MapDeclaration {
  private var pluginDeclarationRootClasses = mutableSetOf<String>()
  private val map = mutableSetOf<String>()
  private val mapped = mutableMapOf<String, String>()
  private var keywordSearches = mutableListOf<SearchDeclarationImpl<LibraryDependency>>()
  private var groupSearches = mutableListOf<SearchDeclarationImpl<LibraryDependency>>()
  private var prefixSearches = mutableListOf<SearchDeclarationImpl<LibraryDependency>>()
  private val searchDefaultOptions = SearchDeclarationImpl<LibraryDependency>()

  @Transient
  private var filters = mutableListOf<(LibraryDependency) -> Boolean>()
  private var filterCount = 0

  override fun transferPluginIds(target: PluginDependencyDeclaration) {
    pluginDeclarationRootClasses += (target as PluginDependencyDeclarationImpl).rootClassName
  }

  override fun transferPluginIds(targetDeclarationName: String) {
    pluginDeclarationRootClasses += targetDeclarationName
  }

  override fun map(vararg dependencies: CharSequence) {
    dependencies.forEach { map.add(Validator.validDependency(it)) }
  }

  override fun map(vararg dependenciesAndPaths: Pair<CharSequence, CharSequence>) {
    dependenciesAndPaths.forEach { (dep, path) -> mapped[Validator.validDependency(dep)] = Validator.validPath(path) }
  }

  override fun search(vararg keywords: CharSequence, declaration: SearchDeclaration<LibraryDependency>.() -> Unit) {
    this.keywordSearches += SearchDeclarationImpl<LibraryDependency>(keywords.map(::String))
      .apply(declaration)
      .copyFrom(searchDefaultOptions)
  }

  override fun searchGroups(vararg groups: CharSequence, declaration: SearchDeclaration<LibraryDependency>.() -> Unit) {
    groupSearches += SearchDeclarationImpl<LibraryDependency>(groups.map(::String))
      .apply(declaration)
      .copyFrom(searchDefaultOptions)
  }

  override fun searchPrefixes(
    vararg prefixes: CharSequence,
    declaration: SearchDeclaration<LibraryDependency>.() -> Unit,
  ) {
    prefixSearches += SearchDeclarationImpl<LibraryDependency>(prefixes.map(::String))
      .apply(declaration)
      .copyFrom(searchDefaultOptions)
  }

  override fun searchDefaultOptions(declaration: SearchDeclaration<LibraryDependency>.() -> Unit) {
    searchDefaultOptions.apply(declaration)
    keywordSearches.forEach { it.copyFrom(searchDefaultOptions) }
    groupSearches.forEach { it.copyFrom(searchDefaultOptions) }
    prefixSearches.forEach { it.copyFrom(searchDefaultOptions) }
  }

  override fun filter(predicate: LibraryDependency.() -> Boolean) {
    filters += predicate
    filterCount++
  }

  override fun toFlow(
    parent: DependencyMapperExtensionImpl,
    formatter: DependencyFormatter,
  ): Flow<MappedDependency> = channelFlow {
    suspend fun List<SearchDeclarationImpl<LibraryDependency>>.sendAllResult(
      type: String,
      callback: DependencyRepositoryClient.(value: String) -> Flow<LibraryDependency>,
    ) {
      if (isEmpty()) return

      // [url, url, ...]
      val urls = joinToString { declaration ->
        declaration.getClients().joinToString { it.baseUrl }
      }.removeBlanks().split(',').toSet().joinToString(prefix = "[", postfix = "]")

      println("Search remote libraries from: $urls by $type...")

      forEachConcurrently { declaration ->
        declaration.getClients().forEachConcurrently { client ->
          declaration.values.forEachConcurrently { value ->
            // Send all search results to the map flow
            sendAll {
              // Call the real client callback to execute the search
              client.callback(value)
                .filter { result -> filters.all { it(result) } && declaration.filters.all { it(result) } }
                .map { MappedDependency("$it:_", formatter.toPath(it)) }
            }
          }
        }
      }
    }

    map.forEachConcurrently { send(MappedDependency("$it:_", formatter.toPath(it))) }
    mapped.forEachConcurrently { (dep, path) -> send(MappedDependency("$dep:_", path)) }
    keywordSearches.sendAllResult("keywords") { fetch(it) }
    groupSearches.sendAllResult("groups") { fetchGroups(it) }
    prefixSearches.sendAllResult("prefixes") { fetchStartsWith(it) }
  }.filter { (notation, _) ->
    val dep = LibraryDependency(notation.removeSuffix(":_"))
    // Transfer the plugin ids to plugin declarations
    if (dep.artifact == "${dep.group}.gradle.plugin") {
      pluginDeclarationRootClasses.map {
        parent.declarations[it] as? PluginDependencyDeclarationImpl
          ?: error("The plugin dependency declaration corresponding to $it was not found")
      }.forEachConcurrently { it.transfer(dep.group) }
    }
    filters.all { it(dep) }
  }
}
