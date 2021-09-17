package com.meowool.gradle.toolkit

import com.meowool.sweekt.iteration.toArray

/**
 * Used to declare how to map plugin dependencies.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
interface PluginDependencyDeclaration {

  /**
   * Adds the given [pluginIds] to map as path.
   *
   * For example:
   * ```
   * map(
   *   "org.mozilla.rust-android-gradle",
   *   "net.bytebuddy.byte-buddy-gradle-plugin",
   * )
   * ```
   */
  fun map(vararg pluginIds: CharSequence)

  /**
   * Adds the given [pluginIds] to map as path.
   *
   * For example:
   * ```
   * map(
   *   "org.mozilla.rust-android-gradle",
   *   "net.bytebuddy.byte-buddy-gradle-plugin",
   * )
   * ```
   */
  fun map(pluginIds: Iterable<CharSequence>) = map(*pluginIds.toArray())

  /**
   * Adds the given pair to map the plugin ids to the specified mapped paths.
   *
   * For example:
   * ```
   * map(
   *   "org.mozilla.rust-android-gradle" to "Mozilla.Rust.AndroidPlugin",
   *   "net.bytebuddy.byte-buddy-gradle-plugin" to "Bytebuddy.GradlePlugin",
   * )
   * ```
   *
   * @param pluginIdsAndPaths The first parameter of pairs is plugin ids, the second parameter is mapped paths.
   */
  fun map(vararg pluginIdsAndPaths: Pair<CharSequence, CharSequence>)

  /**
   * Adds the given pair to map the plugin ids to the specified mapped paths.
   *
   * For example:
   * ```
   * map(
   *   "org.mozilla.rust-android-gradle" to "Mozilla.Rust.AndroidPlugin",
   *   "net.bytebuddy.byte-buddy-gradle-plugin" to "Bytebuddy.GradlePlugin",
   * )
   * ```
   *
   * @param pluginIdsAndPaths The first parameter of pairs is plugin ids, the second parameter is mapped paths.
   */
  fun map(pluginIdsAndPaths: Map<CharSequence, CharSequence>) = map(*pluginIdsAndPaths.toList().toArray())

  /**
   * Adds a request to searches remote plugin ids by [keywords].
   *
   * @param keywords The keywords of remote plugin ids to be mapped.
   * @param declaration Declare how to search for plugin ids.
   */
  fun search(vararg keywords: CharSequence, declaration: SearchDeclaration<PluginId>.() -> Unit = {})

  /**
   * Adds a request to searches remote plugin ids by [keywords].
   *
   * @param keywords The keywords of remote plugin ids to be mapped.
   * @param declaration Declare how to search for plugin ids.
   */
  fun search(keywords: Iterable<CharSequence>, declaration: SearchDeclaration<PluginId>.() -> Unit = {}) =
    search(*keywords.toArray(), declaration = declaration)

  /**
   * Adds a request to searches for remote plugin ids where the specified [prefixes] exist.
   *
   * @param prefixes The prefixes of remote plugin ids to be mapped.
   * @param declaration Declare how to search for plugin ids.
   */
  fun searchPrefixes(vararg prefixes: CharSequence, declaration: SearchDeclaration<PluginId>.() -> Unit = {})

  /**
   * Adds a request to searches for remote plugin ids where the specified [prefixes] exist.
   *
   * @param prefixes The prefixes of remote plugin ids to be mapped.
   * @param declaration Declare how to search for plugin ids.
   */
  fun searchPrefixes(prefixes: Iterable<CharSequence>, declaration: SearchDeclaration<PluginId>.() -> Unit = {}) =
    searchPrefixes(*prefixes.toArray(), declaration = declaration)

  /**
   * Adds a default search options block.
   * All search requests will use the specified [declaration] by default.
   */
  fun searchDefaultOptions(declaration: SearchDeclaration<PluginId>.() -> Unit)

  /**
   * If the [predicate] is `true`, the corresponding plugin id will be mapped, otherwise it will not be mapped.
   */
  fun filter(predicate: PluginId.() -> Boolean)

  /**
   * If the [predicate] is `false`, the corresponding plugin id will be mapped, otherwise it will not be mapped.
   */
  fun filterNot(predicate: PluginId.() -> Boolean) = filter { predicate().not() }

  companion object {
    const val DefaultRootClassName = "Plugins"
  }
}