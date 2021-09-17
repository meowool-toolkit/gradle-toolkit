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
@file:JvmName("Versions")

package de.fayard.refreshVersions.core

import de.fayard.refreshVersions.core.internal.RefreshVersionsConfigHolder
import de.fayard.refreshVersions.core.internal.getVersionPropertyName
import de.fayard.refreshVersions.core.internal.resolveVersion

fun versionFor(versionKey: String): String {
  // This function is overloaded to allow named parameter usage in Kotlin.
  // However, no check is performed here because we cannot detect if
  // the function wasn't called with named argument.
  return retrieveVersionFor(dependencyNotationOrVersionKey = versionKey)
}

fun versionFor(dependencyNotation: CharSequence): String {
  // This function is overloaded to allow named parameter usage in Kotlin.
  // However, no check is performed here because we cannot detect if
  // the function wasn't called with named argument.
  return retrieveVersionFor(dependencyNotationOrVersionKey = dependencyNotation)
}

private fun retrieveVersionFor(dependencyNotationOrVersionKey: CharSequence): String {
  val isDependencyNotation = ':' in dependencyNotationOrVersionKey
  val versionKey = when {
    isDependencyNotation -> {
      require(dependencyNotationOrVersionKey.endsWith(":_")) {
        "Expects a refreshVersions compatible dependency notation with the version placeholder (_)." +
          "\n" +
          "If the dependency is from BoM, " +
          "pass the BoM dependency notation itself with the version placeholder."
      }
      dependencyNotationOrVersionKey.toString().let {
        getVersionPropertyName(
          moduleId = ModuleId(
            group = it.substringBefore(':'),
            name = it.substringBeforeLast(':').substringAfter(':')
          ),
          versionKeyReader = RefreshVersionsConfigHolder.versionKeyReader
        )
      }
    }
    else -> dependencyNotationOrVersionKey.toString().also {
      require(it.startsWith("version.") || it.startsWith("plugin.")) {
        "Version keys all start with 'version.' or 'plugin.'. You need to pass the full version key."
      }
    }
  }
  return resolveVersion(
    properties = RefreshVersionsConfigHolder.lastlyReadVersionsMap,
    key = versionKey
  ) ?: resolveVersion(
    properties = RefreshVersionsConfigHolder.readVersionsMap(),
    key = versionKey
  ) ?: RefreshVersionsConfigHolder.versionsPropertiesFile.name.let { versionsFileName ->
    val errorMessage = when {
      isDependencyNotation ->
        "The version of the artifact $dependencyNotationOrVersionKey requested in " +
          "versionFor call wasn't found in the $versionsFileName file.\n" +
          "Expected a value for the corresponding key: $versionKey"
      else ->
        "The version for the key $versionKey requested in " +
          "versionFor call wasn't found in the $versionsFileName file"
    }
    error(errorMessage)
  }
}
