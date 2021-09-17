package com.meowool.gradle.toolkit

import kotlinx.serialization.Serializable

/**
 * Represents a plugin dependency meta-information.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
@Serializable
class PluginId internal constructor(private val id: CharSequence): CharSequence by id {

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