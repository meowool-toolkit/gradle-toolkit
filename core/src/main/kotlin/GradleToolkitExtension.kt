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
 *
 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
@file:Suppress("SpellCheckingInspection")

package com.meowool.gradle.toolkit

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.repositories

/**
 * An extension of the gradle toolkit belonging to [rootProject].
 *
 * @author 凛 (RinOrz)
 */
interface GradleToolkitExtension {

  /**
   * Returns the root project.
   */
  val rootProject: Project

  /**
   * Returns the all projects of root project.
   *
   * @see Project.getAllprojects
   */
  val allprojects: Set<Project>

  /**
   * Returns the sub-projects of root project.
   *
   * @see Project.getSubprojects
   */
  val subprojects: Set<Project>

  /**
   * The default JVM target for all projects.
   * This value defaults to `11`.
   */
  var defaultJvmTarget: JavaVersion

  /**
   * Executes [action] of each all projects.
   *
   * @param afterEvaluate Performs [action] after project evaluation.
   * @param filter Only when the filter block returns `true` will the [action] be performed for the
   *   corresponding project.
   *
   * @see Project.allprojects
   */
  fun allprojects(
    afterEvaluate: Boolean = false,
    filter: Project.() -> Boolean = { buildFile.exists() },
    action: Project.() -> Unit
  )

  /**
   * Executes [action] of each sub-projects.
   *
   * @param afterEvaluate Performs [action] after subproject evaluation.
   * @param filter Only when the filter block returns `true` will the [action] be performed for the
   *   corresponding subproject.
   *
   * @see Project.subprojects
   */
  fun subprojects(
    afterEvaluate: Boolean = false,
    filter: Project.() -> Boolean = { buildFile.exists() },
    action: Project.() -> Unit
  )

  /**
   * Configures the repositories for all projects.
   *
   * Executes the given [configuration] block against the [RepositoryHandler] for all projects.
   *
   * @see Project.allprojects
   * @see Project.repositories
   */
  fun allrepositories(configuration: RepositoryHandler.() -> Unit) = allprojects { repositories(configuration) }

  /**
   * Configures the repositories for all sub-projects.
   *
   * Executes the given [configuration] block against the [RepositoryHandler] for all sub-projects.
   *
   * @see Project.subprojects
   * @see Project.repositories
   */
  fun subrepositories(configuration: RepositoryHandler.() -> Unit) = subprojects { repositories(configuration) }

  /**
   * Register shared logic through a given [registry] to reference them in any project.
   *
   * This behavior is also called [Dependency injection](https://en.wikipedia.org/wiki/Dependency_injection).
   *
   * NOTE: Very useful when having the same code logic in
   * [multi-projects](https://docs.gradle.org/current/userguide/intro_multi_project_builds.html), this can greatly
   * improve code conciseness.
   *
   * For example:
   * ```
   * // Root project:
   * registerLogic {
   *   project {
   *     // Define common logic of project top level
   *     ...
   *   }
   *   dependencies {
   *     // Define common logic of dependencies
   *     ...
   *   }
   *   dependencies(key = 1) {
   *     // Define logic of dependencies that can only be injected by key
   *     ...
   *   }
   * }
   *
   * // Then other projects, inject with `injectDependenciesLogic(key = 1)`
   * // They will be injected automatically by default keys.
   * ```
   */
  fun registerLogic(registry: LogicRegistry.() -> Unit)
}
