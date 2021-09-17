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

import de.fayard.refreshVersions.core.DependencyVersionsFetcher
import de.fayard.refreshVersions.core.ModuleId
import de.fayard.refreshVersions.core.Version
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.initialization.Settings

internal object SettingsPluginsUpdatesFinder {

  class UpdatesLookupResult(
    val settings: List<PluginWithVersionCandidates>,
    val buildSrcSettings: List<PluginWithVersionCandidates>
  )

  suspend fun getSettingsPluginUpdates(
    httpClient: OkHttpClient,
    mode: VersionCandidatesResultMode
  ): UpdatesLookupResult {

    val rootProjectSettings = RefreshVersionsConfigHolder.settings
    val buildSrcSettings = RefreshVersionsConfigHolder.buildSrcSettings

    val rootProjectSettingsPlugins = rootProjectSettings.getPluginsList()
    val buildSrcSettingsPlugins = buildSrcSettings?.let { settings ->
      settings.getPluginsList() + rootProjectSettingsPlugins.filter {
        settings.pluginManager.hasPlugin(it.id)
      }
    }

    return coroutineScope {

      rootProjectSettingsPlugins.flatMap { dependency ->
        rootProjectSettings.pluginManagement.repositories.asSequence()
          .filterIsInstance<MavenArtifactRepository>()
          .mapNotNull { repo ->
            val fetcher = DependencyVersionsFetcher(httpClient, dependency, repo)
              ?: return@mapNotNull null
            dependency to fetcher
          }
      }.plus(
        buildSrcSettingsPlugins?.flatMap { dependency ->
          buildSrcSettings.pluginManagement.repositories.asSequence()
            .filterIsInstance<MavenArtifactRepository>()
            .mapNotNull { repo ->
              val fetcher = DependencyVersionsFetcher(httpClient, dependency, repo)
                ?: return@mapNotNull null
              dependency to fetcher
            }
        } ?: emptySequence()
      ).distinctBy { (_, versionsFetcher) ->
        versionsFetcher
      }.groupBy { (_, versionsFetcher) ->
        versionsFetcher.moduleId
      }.mapNotNull { (moduleId: ModuleId, listOfDependencyToVersionsFetcher) ->
        Triple(
          first = moduleId,
          second = listOfDependencyToVersionsFetcher.firstOrNull()?.first?.version
            ?: return@mapNotNull null,
          third = listOfDependencyToVersionsFetcher.map { (_, versionsFetcher) -> versionsFetcher }
        )
      }.mapNotNull { (moduleId: ModuleId, currentVersion, versionsFetchers) ->
        val pluginId = moduleId.group ?: return@mapNotNull null
        async {
          PluginWithVersionCandidates(
            pluginId = pluginId,
            currentVersion = currentVersion,
            versionsCandidates = versionsFetchers.getVersionCandidates(
              currentVersion = Version(currentVersion),
              resultMode = mode
            )
          )
        }
      }.awaitAll().let { pluginsWithVersionCandidates ->
        UpdatesLookupResult(
          settings = pluginsWithVersionCandidates.filter {
            rootProjectSettings.pluginManager.hasPlugin(it.pluginId)
          },
          buildSrcSettings = buildSrcSettings?.let { settings ->
            pluginsWithVersionCandidates.filter {
              settings.pluginManager.hasPlugin(it.pluginId)
            }
          } ?: emptyList()
        )
      }
    }
  }

  private const val pluginSuffix = ".gradle.plugin"

  private val Dependency.id get() = group!!

  private fun Settings.getPluginsList(): Sequence<Dependency> {
    return buildscript.configurations.getByName("classpath").dependencies.asSequence().filter {
      it.name.endsWith(pluginSuffix) &&
        it.group == it.name.substringBefore(pluginSuffix) &&
        pluginManager.hasPlugin(it.group!!)
    }
  }
}
