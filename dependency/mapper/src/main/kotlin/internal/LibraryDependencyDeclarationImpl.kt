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
@file:Suppress("NestedLambdaShadowedImplicitParameter", "NAME_SHADOWING")

package com.meowool.gradle.toolkit.internal

import com.meowool.gradle.toolkit.DependencyFormatter
import com.meowool.gradle.toolkit.LibraryDependency
import com.meowool.gradle.toolkit.LibraryDependencyDeclaration
import com.meowool.gradle.toolkit.PluginDependencyDeclaration
import com.meowool.gradle.toolkit.SearchDeclaration
import com.meowool.gradle.toolkit.internal.BaseSearchDeclarationImpl.Data.Companion.clientUrls
import com.meowool.sweekt.String
import com.meowool.sweekt.takeIfNotEmpty
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.gradle.api.Project

/**
 * @author 凛 (https://github.com/RinOrz)
 */
internal class LibraryDependencyDeclarationImpl(rootClassName: String) : LibraryDependencyDeclaration {
  val data = Data(rootClassName)

  override fun transferPluginIds(target: PluginDependencyDeclaration) {
    data.pluginIdsTransferTargets += (target as PluginDependencyDeclarationImpl).data.rootClassName
  }

  override fun transferPluginIds(targetRootClassName: String) {
    data.pluginIdsTransferTargets += targetRootClassName
  }

  override fun map(vararg dependencies: CharSequence) {
    dependencies.forEach {
      data.map.add(Validator.validDependency(it))
    }
  }

  override fun map(vararg dependenciesAndPaths: Pair<CharSequence, CharSequence>) {
    dependenciesAndPaths.forEach { (dep, path) ->
      data.mapped[Validator.validDependency(dep)] = Validator.validPath(path)
    }
  }

  override fun search(vararg keywords: CharSequence, declaration: SearchDeclaration<LibraryDependency>.() -> Unit) {
    data.searchKeywords += SearchDeclarationImpl(keywords.map(::String))
      .apply(declaration).data
      .merge(data.searchDefaultOptions)
  }

  override fun searchGroups(vararg groups: CharSequence, declaration: SearchDeclaration<LibraryDependency>.() -> Unit) {
    data.searchGroups += SearchDeclarationImpl(groups.map(::String))
      .apply(declaration).data
      .merge(data.searchDefaultOptions)
  }

  override fun searchPrefixes(
    vararg prefixes: CharSequence,
    declaration: SearchDeclaration<LibraryDependency>.() -> Unit,
  ) {
    data.searchPrefixes += SearchDeclarationImpl(prefixes.map(::String))
      .apply(declaration).data
      .merge(data.searchDefaultOptions)
  }

  override fun searchDefaultOptions(declaration: SearchDeclaration<LibraryDependency>.() -> Unit) {
    val add = SearchDeclarationImpl().apply(declaration).data
    data.searchDefaultOptions.merge(add)
    data.searchKeywords.forEach { it.merge(add) }
    data.searchGroups.forEach { it.merge(add) }
    data.searchPrefixes.forEach { it.merge(add) }
  }

  override fun filter(predicate: LibraryDependency.() -> Boolean) {
    data.filters += predicate
    data.filterCount++
  }

  internal class SearchDeclarationImpl(
    values: List<String> = emptyList()
  ) : BaseSearchDeclarationImpl<LibraryDependency>(values) {
    override fun convertFilter(original: (LibraryDependency) -> Boolean): (LibraryDependency) -> Boolean = original
  }

  @Serializable
  data class Data(
    val rootClassName: String,
    val map: MutableSet<String> = mutableSetOf(),
    val mapped: MutableMap<String, String> = mutableMapOf(),
    val pluginIdsTransferTargets: MutableSet<String> = mutableSetOf(),
    val searchKeywords: MutableList<BaseSearchDeclarationImpl.Data> = mutableListOf(),
    val searchGroups: MutableList<BaseSearchDeclarationImpl.Data> = mutableListOf(),
    val searchPrefixes: MutableList<BaseSearchDeclarationImpl.Data> = mutableListOf(),
    val searchDefaultOptions: BaseSearchDeclarationImpl.Data = BaseSearchDeclarationImpl.Data(),
    var filterCount: Int = 0,
  ) : DependencyCollector {
    @Transient
    val filters: MutableList<(LibraryDependency) -> Boolean> = mutableListOf()

    override suspend fun ConcurrentScope<*>.collect(
      project: Project,
      output: DependencyMapperInternal.DependencyOutputList
    ) {
      map.forEachConcurrently(capacity = 500) { output.libraries += it }

      mapped.forEachConcurrently(capacity = 500) { dependency, mappedPath ->
        output.mappedLibraries[dependency] = mappedPath
      }

      searchKeywords.clientUrls().takeIfNotEmpty()?.also { urls ->
        project.logger.quiet("Search remote plugins from: [$urls] by keywords...")
        searchKeywords.forEachConcurrently {
          it.searchKeywords().collect { output.libraries += it.toString() }
        }
      }

      searchPrefixes.clientUrls().takeIfNotEmpty()?.also { urls ->
        project.logger.quiet("Search remote plugins from: [$urls] by prefixes...")
        searchPrefixes.forEachConcurrently {
          it.searchPrefixes().collect { output.libraries += it.toString() }
        }
      }

      searchGroups.clientUrls().takeIfNotEmpty()?.also { urls ->
        project.logger.quiet("Search remote libraries from: [$urls] by groups...")
        searchGroups.forEachConcurrently {
          it.searchGroups().collect {
            output.libraries += it.toString()
          }
        }
      }
    }

    override suspend fun ConcurrentScope<*>.collect(project: Project, pool: JarPool, formatter: DependencyFormatter) {
      suspend fun sendMap(dependency: CharSequence, mappedPath: CharSequence = formatter.toPath(dependency)) {
        val dependency = dependency as? LibraryDependency ?: LibraryDependency(dependency)

        // Do not send if any filter predicate is false
        if (filters.any { it(dependency).not() }) return

        // Transfer the plugin ids to plugin declarations
        dependency.toPluginIdOrNull()?.also { pluginId ->
          pluginIdsTransferTargets.forEach {
            // We format plugin-id here, don’t use `mappedPath` directly, because it belongs to `group.id:artifact.id`
            pool.pluginsJar(it).addDependencyField(formatter.toPath(pluginId), value = pluginId)
          }
        }

        pool.librariesJar(rootClassName).addDependencyField(
          fullPath = mappedPath, // Group.Id.Artifact.Id
          value = "$dependency:_" // group.id:artifact.id:_
        )
      }

      map.forEachConcurrently(capacity = 500, ::sendMap)

      mapped.forEachConcurrently(capacity = 500, ::sendMap)

      searchKeywords.clientUrls().takeIfNotEmpty()?.also { urls ->
        project.logger.quiet("Search remote libraries from: [$urls] by keywords...")
        searchKeywords.forEachConcurrently { it.searchKeywords().collect(::sendMap) }
      }

      searchPrefixes.clientUrls().takeIfNotEmpty()?.also { urls ->
        project.logger.quiet("Search remote libraries from: [$urls] by prefixes...")
        searchPrefixes.forEachConcurrently { it.searchPrefixes().collect(::sendMap) }
      }

      searchGroups.clientUrls().takeIfNotEmpty()?.also { urls ->
        project.logger.quiet("Search remote libraries from: [$urls] by groups...")
        searchGroups.forEachConcurrently { it.searchGroups().collect(::sendMap) }
      }
    }
  }
}
