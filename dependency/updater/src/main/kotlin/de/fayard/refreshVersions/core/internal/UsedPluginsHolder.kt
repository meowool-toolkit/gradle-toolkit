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
package de.fayard.refreshVersions.core.internal

import org.gradle.api.artifacts.ArtifactRepositoryContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalDependency
import org.gradle.api.internal.artifacts.dependencies.AbstractDependency

internal object UsedPluginsHolder {

  fun noteUsedPluginDependency(
    dependencyNotation: String,
    repositories: ArtifactRepositoryContainer
  ) {
    synchronized(lock) {
      usedPluginDependencies += UsedPluginDependency(
        dependencyNotation = dependencyNotation,
        repositories = repositories
      )
    }
  }

  fun read(): Sequence<Pair<Dependency, ArtifactRepositoryContainer>> {
    return usedPluginDependencies.asSequence().map {
      ConfigurationLessDependency(it.dependencyNotation) to it.repositories
    }
  }

  fun pluginHasNoEntryInVersionsFile(dependency: ExternalDependency) {
    synchronized(lock) {
      _usedPluginsWithoutEntryInVersionsFile.add(dependency)
    }
  }
  val usedPluginsWithoutEntryInVersionsFile: List<ExternalDependency>
    get() = _usedPluginsWithoutEntryInVersionsFile

  private val _usedPluginsWithoutEntryInVersionsFile by RefreshVersionsConfigHolder.resettableDelegates.Lazy {
    mutableListOf<ExternalDependency>()
  }

  private val lock = Any()

  private data class UsedPluginDependency(
    val dependencyNotation: String,
    val repositories: ArtifactRepositoryContainer
  )

  private val usedPluginDependencies by RefreshVersionsConfigHolder.resettableDelegates.Lazy {
    mutableListOf<UsedPluginDependency>()
  }

  private class ConfigurationLessDependency(val dependencyNotation: String) : AbstractDependency() {

    override fun getGroup() = group
    override fun getName() = name
    override fun getVersion(): String? = version

    override fun contentEquals(dependency: Dependency): Boolean = throw UnsupportedOperationException()
    override fun copy(): Dependency = ConfigurationLessDependency(dependencyNotation)

    private val group = dependencyNotation.substringBefore(':').unwrappedNullableValue()
    private val name = dependencyNotation.substringAfter(':').substringBefore(':')
    private val version = dependencyNotation.substringAfterLast(':').unwrappedNullableValue()

    private fun String.unwrappedNullableValue(): String? = if (this == "null") null else this

    override fun toString() = "$group:$name:$version"
  }
}
