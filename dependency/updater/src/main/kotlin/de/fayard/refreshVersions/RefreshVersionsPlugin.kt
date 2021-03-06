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
package de.fayard.refreshVersions

import de.fayard.refreshVersions.core.RefreshVersionsCorePlugin
import de.fayard.refreshVersions.core.bootstrapRefreshVersionsCore
import de.fayard.refreshVersions.core.bootstrapRefreshVersionsCoreForBuildSrc
import de.fayard.refreshVersions.core.extensions.gradle.isBuildSrc
import de.fayard.refreshVersions.core.internal.RefreshVersionsConfigHolder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

open class RefreshVersionsPlugin : Plugin<Any> {

  companion object {
    @JvmStatic
    val artifactVersionKeyRules: List<String> = listOf(
      "androidx-version-alias-rules",
      "google-version-alias-rules",
      "kotlin(x)-version-alias-rules",
      "square-version-alias-rules",
      "testing-version-alias-rules",
      "dependency-groups-alias-rules"
    ).map {
      RefreshVersionsPlugin::class.java.getResourceAsStream("/refreshVersions-rules/$it.txt")!!
        .bufferedReader()
        .readText()
    }
  }

  override fun apply(target: Any) {
    require(target is Settings) {
      val notInExtraClause: String = when (target) {
        is Project -> when (target) {
          target.rootProject -> ", not in build.gradle(.kts)"
          else -> ", not in a build.gradle(.kts) file."
        }
        is Gradle -> ", not in an initialization script."
        else -> ""
      }
      """
            plugins.id("de.fayard.refreshVersions") must be configured in settings.gradle(.kts)$notInExtraClause.
            See https://jmfayard.github.io/refreshVersions/setup/
      """.trimIndent()
    }
    bootstrap(target)
  }

  private fun bootstrap(settings: Settings) {
    RefreshVersionsConfigHolder.markSetupViaSettingsPlugin()
    if (settings.extensions.findByName("refreshVersions") == null) {
      // If using legacy bootstrap, the extension has already been created.
      settings.extensions.create<RefreshVersionsExtension>("refreshVersions")
    }

    if (settings.isBuildSrc) {
      settings.bootstrapRefreshVersionsCoreForBuildSrc()
      addDependencyToBuildSrcForGroovyDsl(settings)
      return
    }
    settings.gradle.settingsEvaluated {

      val extension: RefreshVersionsExtension = extensions.getByType()

      bootstrapRefreshVersionsCore(
        artifactVersionKeyRules = if (extension.extraArtifactVersionKeyRules.isEmpty()) {
          artifactVersionKeyRules // Avoid unneeded list copy.
        } else {
          artifactVersionKeyRules + extension.extraArtifactVersionKeyRules
        },
        versionsPropertiesFile = extension.versionsPropertiesFile
          ?: settings.rootDir.resolve("versions.properties")
      )
      if (extension.isBuildSrcLibsEnabled) gradle.beforeProject {
        if (project != project.rootProject) return@beforeProject

        fun plugin(id: String, version: String) = "$id:$id.gradle.plugin:$version"

        buildscript.repositories.addAll(settings.pluginManagement.repositories)
        val dependencyNotation = plugin(
          id = "de.fayard.buildSrcLibs",
          version = RefreshVersionsCorePlugin.currentVersion
        )
        buildscript.dependencies.add("classpath", dependencyNotation)

        afterEvaluate {
          apply(plugin = "de.fayard.buildSrcLibs")
        }
      }
      gradle.rootProject {
        applyToProject(this)
      }
    }
  }

  private fun applyToProject(project: Project) {
    if (project != project.rootProject) return // We want the tasks only for the root project

    project.tasks.register<RefreshVersionsDependenciesMigrationTask>(
      name = "migrateToRefreshVersionsDependenciesConstants"
    ) {
      group = "refreshVersions"
      description = "Assists migration from hardcoded dependencies to constants of " +
        "the refreshVersions dependencies plugin"
      finalizedBy("refreshVersions")
    }

        /* // TODO: Find out whether we want to expose the task or not.
        project.tasks.register<MissingEntriesTask>(
            name = "refreshVersionsMissingEntries"
        ) {
            group = "refreshVersions"
            description = "Add missing entries to 'versions.properties'"
            outputs.upToDateWhen { false }
        }
        */
    project.tasks.register<RefreshVersionsMigrateTask>(
      name = "refreshVersionsMigrate"
    ) {
      group = "refreshVersions"
      description = "Migrate build to refreshVersions"
    }
  }

  private fun addDependencyToBuildSrcForGroovyDsl(settings: Settings) {
    require(settings.isBuildSrc)
    settings.gradle.rootProject {
      repositories.addAll(settings.pluginManagement.repositories)

      fun plugin(id: String, version: String): String {
        return "$id:$id.gradle.plugin:$version"
      }

      dependencies {
        "implementation"(plugin("de.fayard.refreshVersions", RefreshVersionsCorePlugin.currentVersion))
      }
    }
  }
}
