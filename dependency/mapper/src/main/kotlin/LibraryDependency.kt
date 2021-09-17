package com.meowool.gradle.toolkit

import com.meowool.sweekt.substringAfter
import com.meowool.sweekt.substringBefore
import kotlinx.serialization.Serializable

/**
 * Represents a library dependency meta-information.
 *
 * For example group is `foo.bar`, artifact is `gav`:
 * ```
 * Dependency("foo.bar:gav")
 * ```
 *
 * @author 凛 (https://github.com/RinOrz)
 */
@Serializable
class LibraryDependency internal constructor(private val notation: CharSequence): CharSequence by notation {
  val group: String = notation.substringBefore(':')
  val artifact: String = notation.substringAfter(':')

  constructor(group: String, artifact: String) : this("$group:$artifact")

  /**
   * Converts this library dependency to plugin id.
   *
   * The gradle find the coordinates by the following marker:
   * `pluginId:pluginId.gradle.plugin:version`
   *
   * For more details, see (Plugin Marker Artifacts)[https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_markers]
   */
  fun toPluginIdOrNull(): PluginId? = if (artifact == "$group.gradle.plugin") PluginId(group) else null

  override fun hashCode(): Int = toString().hashCode()
  override fun equals(other: Any?): Boolean = toString() == other?.toString()
  override fun toString(): String = notation.toString()
}