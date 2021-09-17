package com.meowool.gradle.toolkit.internal

import com.meowool.gradle.toolkit.DependencyFormatter
import com.meowool.gradle.toolkit.LibraryDependency
import com.meowool.gradle.toolkit.PluginDependencyDeclaration
import com.meowool.gradle.toolkit.PluginId
import com.meowool.gradle.toolkit.SearchDeclaration
import com.meowool.gradle.toolkit.internal.client.DependencyRepositoryClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.Transient
import kotlinx.serialization.Serializable
import com.meowool.sweekt.String
import com.meowool.sweekt.removeBlanks

/**
 * @author 凛 (https://github.com/RinOrz)
 */
@Serializable
internal class PluginDependencyDeclarationImpl(
  override val rootClassName: String
) : PluginDependencyDeclaration, MapDeclaration {
  private val map = mutableSetOf<String>()
  private val mapped = mutableMapOf<String, String>()
  private val keywordSearches = mutableListOf<SearchDeclarationImpl<PluginId>>()
  private val prefixSearches = mutableListOf<SearchDeclarationImpl<PluginId>>()
  private val searchDefaultOptions = SearchDeclarationImpl<PluginId>()

  @Transient
  private val transfer = mutableSetOf<String>()
  @Transient
  private val filters = mutableListOf<(PluginId) -> Boolean>()
  private var filterCount = 0

  fun transfer(pluginId: CharSequence) = transfer.add(pluginId.toString())

  override fun map(vararg pluginIds: CharSequence) {
    pluginIds.forEach { map.add(Validator.validPluginId(it)) }
  }

  override fun map(vararg pluginIdsAndPaths: Pair<CharSequence, CharSequence>) {
    pluginIdsAndPaths.forEach { (id, path) -> mapped[Validator.validPluginId(id)] = Validator.validPath(path) }
  }

  override fun search(vararg keywords: CharSequence, declaration: SearchDeclaration<PluginId>.() -> Unit) {
    keywordSearches += SearchDeclarationImpl<PluginId>(keywords.map(::String))
      .apply(declaration)
      .copyFrom(searchDefaultOptions)
  }

  override fun searchPrefixes(vararg prefixes: CharSequence, declaration: SearchDeclaration<PluginId>.() -> Unit) {
    prefixSearches += SearchDeclarationImpl<PluginId>(prefixes.map(::String))
      .apply(declaration)
      .copyFrom(searchDefaultOptions)
  }

  override fun searchDefaultOptions(declaration: SearchDeclaration<PluginId>.() -> Unit) {
    searchDefaultOptions.apply(declaration)
    keywordSearches.forEach { it.copyFrom(searchDefaultOptions) }
    prefixSearches.forEach { it.copyFrom(searchDefaultOptions) }
  }

  override fun filter(predicate: PluginId.() -> Boolean) {
    filters += predicate
    filterCount++
  }

  override fun toFlow(
    formatter: DependencyFormatter
  ): Flow<MappedDependency> = channelFlow {
    suspend fun List<SearchDeclarationImpl<PluginId>>.sendAllResult(
      type: String,
      callback: DependencyRepositoryClient.(value: String) -> Flow<LibraryDependency>
    ) {
      if (isEmpty()) return

      // [url, url, ...]
      val urls = joinToString { declaration ->
        declaration.getClients().joinToString { it.baseUrl }
      }.removeBlanks().split(',').toSet().joinToString(prefix = "[", postfix = "]")

      println("Search remote plugins from: $urls by $type...")

      forEachConcurrently { declaration ->
        declaration.getClients().forEachConcurrently { client ->
          declaration.values.forEachConcurrently { value ->
            // Send all search results to the map flow
            sendAll {
              // Call the real client callback to execute the search
              client.callback(value)
                .mapNotNull { it.toPluginIdOrNull() }
                .filter { result -> filters.all { it(result) } && declaration.filters.all { it(result) } }
                .map { MappedDependency(it, formatter.toPath(it)) }
            }
          }
        }
      }
    }

    (transfer + map).forEachConcurrently { send(MappedDependency(it, formatter.toPath(it))) }
    mapped.forEachConcurrently { (dep, path) -> send(MappedDependency(dep, path)) }
    keywordSearches.sendAllResult("keywords") { fetch(it) }
    prefixSearches.sendAllResult("prefixes") { fetchStartsWith(it) }
  }
}