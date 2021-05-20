/*
 * Copyright (c) $\YEAR. The Meowool Organization Open Source Project
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
 */
@file:Suppress("SpellCheckingInspection")

/**
 * Resolve all defined dependencies.
 *
 * ```
 * androidx.compose.ui:ui
 * androidx.activity:activity-compose
 * androidx.ads:ads-identifier [parent=Ads, name=Identifier]
 * ```
 */
internal fun String.resolveDependencies(groupReplacer: GroupReplacer): List<Dependency> {
  // Define `[parent=Ads, name=Identifier]`
  data class Customization(val parent: String?, val name: String?)

  // Get a customize statement: `[parent=..., name=...]`
  fun String.getCustomization(): Customization? {
    if (!this.contains('[')) return null
    var parent: String? = null
    var name: String? = null
    this
      .substringAfterLast('[')
      .removeSuffix("]")
      .removeBlanks()
      .split(',')
      .forEach {
        require(it.contains('=')) { "Illegal dependency custom declaration: $it" }
        // Split key and value: `key=value`
        val kv = it.split('=')
        val value = kv.getOrNull(1)
        when (kv[0]) {
          "parent" -> parent = value
          "name" -> name = value
        }
      }
    return Customization(parent, name)
  }

  return lines().mapNotNull {
    // Skip comment line
    if (it.startsWith("//") || it.startsWith("#") || it.startsWith("/*") || it.isBlank()) return@mapNotNull null

    val row = it.removeBlanks().substringBeforeLast("[")

    require(row.contains(':')) { "Illegal dependency declaration line: $it" }

    val customization = it.getCustomization()
    val (group, artifact) = row.split(':')

    Dependency(
      oldParent = customization?.parent ?: groupReplacer[group] ?: group,
      oldName = customization?.name ?: groupReplacer[artifact] ?: artifact,
      full = row,
    )
  }
}

/**
 * Resolve rule definition of dependencies group.
 *
 * ```
 * androidx.compose.ui -> androidx.compose
 * androidx.activity -> androidx
 * androidx.appcompat -> androidx
 * ```
 */
internal fun String.resolveDependencyGroupRules(): GroupReplacer = lines().map {
  val (old, new) = it.removeBlanks().split("->")
  old to new
}.toMap()

/**
 * Used to replace group id according to rule
 * `androidx.appcompat -> androidx`
 */
internal typealias GroupReplacer = Map<String, String>
