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
package de.fayard.refreshVersions.core.internal

import de.fayard.refreshVersions.core.DependencyVersionsFetcher
import de.fayard.refreshVersions.core.ModuleId
import de.fayard.refreshVersions.core.Version
import de.fayard.refreshVersions.core.extensions.gradle.isGradlePlugin
import de.fayard.refreshVersions.core.extensions.gradle.moduleId
import de.fayard.refreshVersions.core.extensions.gradle.toModuleIdentifier
import de.fayard.refreshVersions.core.internal.versions.VersionsPropertiesModel
import de.fayard.refreshVersions.core.internal.versions.writeWithNewEntry
import kotlinx.coroutines.runBlocking
import org.gradle.api.Project
import org.gradle.api.artifacts.ArtifactRepositoryContainer
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ExternalDependency
import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.invocation.Gradle

internal const val versionPlaceholder = "_"

internal fun Gradle.setupVersionPlaceholdersResolving(versionsMap: Map<String, String>) {

  val versionKeyReader = RefreshVersionsConfigHolder.versionKeyReader
  var currentVersionsMap: Map<String, String> = versionsMap
  val refreshVersionsMap = { updatedMap: Map<String, String> ->
    currentVersionsMap = updatedMap
  }
  beforeProject {
    val project: Project = this@beforeProject

    fun replaceVersionPlaceholdersFromDependencies(
      configuration: Configuration,
      isFromBuildscript: Boolean
    ) {
      if (configuration.name in configurationNamesToIgnore) return

      configuration.replaceVersionPlaceholdersFromDependencies(
        project = project,
        isFromBuildscript = isFromBuildscript,
        versionKeyReader = versionKeyReader,
        initialVersionsMap = currentVersionsMap,
        refreshVersionsMap = refreshVersionsMap
      )
    }

    project.buildscript.configurations.configureEach {
      replaceVersionPlaceholdersFromDependencies(
        configuration = this,
        isFromBuildscript = true
      )
    }

    configurations.configureEach {
      replaceVersionPlaceholdersFromDependencies(
        configuration = this,
        isFromBuildscript = false
      )
    }
  }
}

private val configurationNamesToIgnore: List<String> = listOf(
  "embeddedKotlin",
  "kotlinCompilerPluginClasspath",
  "kotlinCompilerClasspath"
)

@InternalRefreshVersionsApi
fun getVersionPropertyName(
  moduleId: ModuleId,
  versionKeyReader: ArtifactVersionKeyReader
): String = getVersionPropertyName(
  moduleIdentifier = moduleId.toModuleIdentifier(),
  versionKeyReader = versionKeyReader
)

@InternalRefreshVersionsApi
fun getVersionPropertyName(
  moduleIdentifier: ModuleIdentifier,
  versionKeyReader: ArtifactVersionKeyReader
): String {

  val group = moduleIdentifier.group
  val name = moduleIdentifier.name

  // TODO: Pos pluginDependencyNotationToVersionKey ?
  return when {
    name == "gradle" && group == "com.android.tools.build" -> "plugin.android"
    moduleIdentifier.isGradlePlugin -> {
      name.substringBeforeLast(".gradle.plugin").let { pluginId ->
        when {
          pluginId.startsWith("org.jetbrains.kotlin.") -> "version.kotlin"
          pluginId.startsWith("com.android") -> "plugin.android"
          else -> "plugin.$pluginId"
        }
      }
    }
    else -> {
      val versionKey = versionKeyReader.readVersionKey(group = group, name = name) ?: "$group..$name"
      "version.$versionKey"
    }
  }
}

internal tailrec fun resolveVersion(
  properties: Map<String, String>,
  key: String,
  redirects: Int = 0
): String? {
  if (redirects > 5) error("Up to five redirects are allowed, for readability. You should only need one.")
  val value = properties[key] ?: return null
  return if (value.isAVersionAlias()) resolveVersion(properties, value, redirects + 1) else value
}

/**
 * Expects the value of a version property (values of the map returned by [RefreshVersionsConfigHolder.readVersionsMap]).
 */
internal fun String.isAVersionAlias(): Boolean = startsWith("version.") || startsWith("plugin.")

private val lock = Any()

private fun Configuration.replaceVersionPlaceholdersFromDependencies(
  project: Project,
  isFromBuildscript: Boolean,
  versionKeyReader: ArtifactVersionKeyReader,
  initialVersionsMap: Map<String, String>,
  refreshVersionsMap: (updatedMap: Map<String, String>) -> Unit
) {

  val repositories = if (isFromBuildscript) project.buildscript.repositories else project.repositories
  var properties = initialVersionsMap
  @Suppress("UnstableApiUsage")
  withDependencies {
    for (dependency in this) {
      if (dependency !is ExternalDependency) continue
      if (dependency.version != versionPlaceholder) continue
      val moduleId = dependency.moduleId
      val propertyName = getVersionPropertyName(moduleId, versionKeyReader)
      val versionFromProperties = resolveVersion(
        properties = properties,
        key = propertyName
      ) ?: synchronized(lock) {
        RefreshVersionsConfigHolder.readVersionsMap().let { updatedMap ->
          properties = updatedMap
          refreshVersionsMap(updatedMap)
        }
        resolveVersion(properties, propertyName)
          ?: `Write versions candidates using latest most stable version and get it`(
            repositories = repositories,
            propertyName = propertyName,
            dependency = dependency
          ).also {
            RefreshVersionsConfigHolder.readVersionsMap().let { updatedMap ->
              properties = updatedMap
              refreshVersionsMap(updatedMap)
            }
          }
      }
      dependency.version {
        require(versionFromProperties)
        reject(versionPlaceholder) // Remember that we're managing the version of this dependency.
      }
    }
  }
}

@Suppress("unused") // Used in the dependencies plugin
@InternalRefreshVersionsApi
fun Project.writeCurrentVersionInProperties(
  versionKey: String,
  currentVersion: String
) {
  VersionsPropertiesModel.writeWithNewEntry(
    propertyName = versionKey,
    versionsCandidates = listOf(Version(currentVersion))
  )
}

@Suppress("FunctionName")
private fun `Write versions candidates using latest most stable version and get it`(
  repositories: ArtifactRepositoryContainer,
  propertyName: String,
  dependency: ExternalDependency
): String = `Write versions candidates using latest most stable version and get it`(
  propertyName = propertyName,
  dependencyVersionsFetchers = repositories.filterIsInstance<MavenArtifactRepository>()
    .mapNotNull { repo ->
      DependencyVersionsFetcher(
        httpClient = RefreshVersionsConfigHolder.httpClient,
        dependency = dependency,
        repository = repo
      )
    }
)

@Suppress("FunctionName")
internal fun `Write versions candidates using latest most stable version and get it`(
  propertyName: String,
  dependencyVersionsFetchers: List<DependencyVersionsFetcher>
): String = runBlocking {
  dependencyVersionsFetchers.getVersionCandidates(
    currentVersion = Version(""),
    resultMode = RefreshVersionsConfigHolder.resultMode
  ).let { versionCandidates ->
    val bestStability = versionCandidates.minByOrNull { it.stabilityLevel }!!.stabilityLevel
    val versionToUse = versionCandidates.last { it.stabilityLevel == bestStability }
    VersionsPropertiesModel.writeWithNewEntry(
      propertyName = propertyName,
      versionsCandidates = versionCandidates.dropWhile { it != versionToUse }
    )
    versionToUse.value
  }
}
