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
@file:Suppress("NAME_SHADOWING", "NestedLambdaShadowedImplicitParameter")

package com.meowool.gradle.toolkit.internal

import com.meowool.gradle.toolkit.DependencyFormatter
import com.meowool.gradle.toolkit.LibraryDependency
import com.meowool.gradle.toolkit.PluginDependencyDeclaration
import com.meowool.gradle.toolkit.PluginId
import com.meowool.gradle.toolkit.SearchDeclaration
import com.meowool.gradle.toolkit.internal.BaseSearchDeclarationImpl.Data.Companion.clientUrls
import com.meowool.sweekt.String
import com.meowool.sweekt.takeIfNotEmpty
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.gradle.api.Project

/**
 * @author 凛 (RinOrz)
 */
internal class PluginDependencyDeclarationImpl(rootClassName: String) : PluginDependencyDeclaration {
  val data = Data(rootClassName)

  override fun map(vararg pluginIds: CharSequence) {
    pluginIds.forEach {
      data.map.add(Validator.validPluginId(it))
    }
  }

  override fun map(vararg pluginIdsAndPaths: Pair<CharSequence, CharSequence>) {
    pluginIdsAndPaths.forEach { (id, path) ->
      data.mapped[Validator.validPluginId(id)] = Validator.validPath(path)
    }
  }

  override fun search(vararg keywords: CharSequence, declaration: SearchDeclaration<PluginId>.() -> Unit) {
    data.searchKeywords += SearchDeclarationImpl(keywords.map(::String))
      .apply(declaration).data
      .merge(data.searchDefaultOptions)
  }

  override fun searchPrefixes(
    vararg prefixes: CharSequence,
    declaration: SearchDeclaration<PluginId>.() -> Unit,
  ) {
    data.searchPrefixes += SearchDeclarationImpl(prefixes.map(::String))
      .apply(declaration).data
      .merge(data.searchDefaultOptions)
  }

  override fun searchDefaultOptions(declaration: SearchDeclaration<PluginId>.() -> Unit) {
    val add = SearchDeclarationImpl().apply(declaration).data
    data.searchDefaultOptions.merge(add)
    data.searchKeywords.forEach { it.merge(add) }
    data.searchPrefixes.forEach { it.merge(add) }
  }

  override fun filter(predicate: PluginId.() -> Boolean) {
    data.filters += predicate
    data.filterCount++
  }

  class SearchDeclarationImpl(
    values: List<String> = emptyList()
  ) : BaseSearchDeclarationImpl<PluginId>(values) {
    override fun convertFilter(original: (PluginId) -> Boolean): (LibraryDependency) -> Boolean = {
      original(it.toPluginIdOrNull()!!)
    }
  }

  @Serializable
  data class Data(
    val rootClassName: String,
    val map: MutableSet<String> = mutableSetOf(),
    val mapped: MutableMap<String, String> = mutableMapOf(),
    val searchKeywords: MutableList<BaseSearchDeclarationImpl.Data> = mutableListOf(),
    val searchPrefixes: MutableList<BaseSearchDeclarationImpl.Data> = mutableListOf(),
    val searchDefaultOptions: BaseSearchDeclarationImpl.Data = BaseSearchDeclarationImpl.Data(),
    var filterCount: Int = 0,
  ) : DependencyCollector {
    @Transient
    val filters: MutableList<(PluginId) -> Boolean> = mutableListOf()

    override suspend fun ConcurrentScope<*>.collect(
      project: Project,
      isConcurrently: Boolean,
      output: DependencyMapperInternal.DependencyOutputList
    ) {
      map.forEachConcurrently(capacity = 500) { output.plugins += it }

      mapped.forEachConcurrently(capacity = 500) { dependency, mappedPath ->
        output.mappedPlugins[dependency] = mappedPath
      }

      searchKeywords.clientUrls().takeIfNotEmpty()?.also { urls ->
        project.logger.quiet("Search remote plugins from: [$urls] by keywords...")
        searchKeywords.forEachConcurrently {
          it.searchKeywords(isConcurrently)
            .mapNotNull { it.toPluginIdOrNull() }
            .collect { output.plugins += it.toString() }
        }
      }

      searchPrefixes.clientUrls().takeIfNotEmpty()?.also { urls ->
        project.logger.quiet("Search remote plugins from: [$urls] by prefixes...")
        searchPrefixes.forEachConcurrently {
          it.searchPrefixes(isConcurrently)
            .mapNotNull { it.toPluginIdOrNull() }
            .collect { output.plugins += it.toString() }
        }
      }
    }

    override suspend fun ConcurrentScope<*>.collect(
      project: Project,
      pool: JarPool,
      isConcurrently: Boolean,
      formatter: DependencyFormatter
    ) {
      suspend fun sendMap(dependency: CharSequence, mappedPath: CharSequence = formatter.toPath(dependency)) {
        var mappedPath = mappedPath
        val pluginId = when (dependency) {
          // For `search**`... Do not send remote dependency that is not a plugin id
          is LibraryDependency -> dependency.toPluginIdOrNull()?.also { mappedPath = formatter.toPath(it) } ?: return
          // For `map` or `mapped`
          else -> PluginId(dependency)
        }

        // Do not send if any filter predicate is false
        if (filters.any { it(pluginId).not() }) return

        pool.pluginsJar(rootClassName).addDependencyField(
          fullPath = mappedPath, // Foo.Bar.Id
          value = pluginId // foo.bar.id
        )
      }

      map.forEachConcurrently(capacity = 500, ::sendMap)

      mapped.forEachConcurrently(capacity = 500, ::sendMap)

      searchKeywords.clientUrls().takeIfNotEmpty()?.also { urls ->
        project.logger.quiet("Search remote plugins from: [$urls] by keywords...")
        searchKeywords.forEachConcurrently { it.searchKeywords(isConcurrently).collect(::sendMap) }
      }

      searchPrefixes.clientUrls().takeIfNotEmpty()?.also { urls ->
        project.logger.quiet("Search remote plugins from: [$urls] by prefixes...")
        searchPrefixes.forEachConcurrently { it.searchPrefixes(isConcurrently).collect(::sendMap) }
      }
    }
  }
}
