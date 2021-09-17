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
  private var pluginDeclarations = mutableSetOf<PluginDependencyDeclarationImpl>()
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
    pluginDeclarations += target as PluginDependencyDeclarationImpl
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
      pluginDeclarations.forEachConcurrently { it.transfer(dep.group) }
    }
    filters.all { it(dep) }
  }
}