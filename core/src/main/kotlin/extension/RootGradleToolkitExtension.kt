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
 */
@file:Suppress("SpellCheckingInspection")

package extension

import importLazyDependencies
import kotlinCompile
import kotlinJvmOptions
import kotlinOptions
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

/**
 * An extension of the root gradle toolkit belonging to [project].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
interface RootGradleToolkitExtension : GradleToolkitExtension {

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
   * Executes [action] of each all projects.
   *
   * @see Project.allprojects
   */
  fun allprojects(action: Project.() -> Unit)

  /**
   * Executes [action] of each sub-projects.
   *
   * @see Project.subprojects
   */
  fun subprojects(action: Project.() -> Unit)

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
   * Uses given [configuration] to configure kotlin common compile task for all projects.
   */
  fun allKotlinCompiles(configuration: KotlinCompile<KotlinCommonOptions>.() -> Unit) =
    allprojects { kotlinCompile(configuration) }

  /**
   * Uses given [configuration] to configure kotlin jvm compile task for all projects.
   */
  fun allKotlinJvmCompiles(configuration: KotlinJvmCompile.() -> Unit) =
    allprojects { kotlinJvmCompile(configuration) }

  /**
   * Configures options for kotlin common compile task for all projects with the given [configuration] block.
   */
  fun allKotlinOptions(configuration: KotlinCommonOptions.() -> Unit) =
    allprojects { kotlinOptions(configuration) }

  /**
   * Configures options for kotlin jvm compile task for all projects with the given [configuration] block.
   */
  fun allKotlinJvmOptions(configuration: KotlinCommonOptions.() -> Unit) =
    allprojects { kotlinJvmOptions(configuration) }

  /**
   * Uses given [configuration] to configure kotlin common compile task for all sub-projects.
   */
  fun subKotlinCompiles(configuration: KotlinCompile<KotlinCommonOptions>.() -> Unit) =
    subprojects { kotlinCompile(configuration) }

  /**
   * Uses given [configuration] to configure kotlin jvm compile task for all sub-projects.
   */
  fun subKotlinJvmCompiles(configuration: KotlinJvmCompile.() -> Unit) =
    subprojects { kotlinJvmCompile(configuration) }

  /**
   * Configures options for kotlin common compile task for all sub-projects with the given [configuration] block.
   */
  fun subKotlinOptions(configuration: KotlinCommonOptions.() -> Unit) =
    subprojects { kotlinOptions(configuration) }

  /**
   * Configures options for kotlin jvm compile task for all sub-projects with the given [configuration] block.
   */
  fun subKotlinJvmOptions(configuration: KotlinCommonOptions.() -> Unit) =
    subprojects { kotlinJvmOptions(configuration) }

  /**
   * Shares a reusable dependencies code [block] into [scope].
   *
   * Unlike [shareLazyDependencies], when the project belongs to the [scope], the shared dependencies are
   * automatically imported.
   *
   * NOTE: Very useful when having the same dependencies in multi-modules,
   * this can greatly improve code conciseness.
   *
   * ```
   * shareDependencies {
   *   // define common dependencies.
   *   implementationOf(...)
   * }
   * shareDependencies("1") {
   *   // define scope-1 dependencies.
   *   implementationOf(...)
   * }
   * ```
   *
   * @param scope representative the effect scope of the dependencies shared block.
   *
   * @see Project.dependencies
   * @see GradleToolkitExtension.scope
   */
  fun shareDependencies(
    scope: String? = null,
    block: DependencyHandler.() -> Unit,
  )

  /**
   * Shares a reusable lazy dependencies code [block] into [scope].
   *
   * Unlike [shareDependencies], you must manually call [importLazyDependencies] to import dependencies.
   *
   * NOTE: Very useful when having the same dependencies in multi-modules, this can greatly
   * improve code conciseness.
   *
   * ```
   * rootProject {
   *   shareLazyDependencies {
   *     // define common lazy dependencies.
   *     implementationOf(...)
   *   }
   *   shareLazyDependencies("1") {
   *     // define scope-1 lazy dependencies.
   *     implementationOf(...)
   *   }
   * }
   *
   * // Other project(s)
   * project {
   *   dependencies {
   *     // Import defined common lazy dependencies.
   *     importLazyDependencies()
   *     // Import defined scope-1 lazy dependencies.
   *     importLazyDependencies("1")
   *   }
   * }
   * ```
   *
   * @param scope representative the effect scope of the dependencies shared block.
   *
   * @see Project.dependencies
   * @see Project.importLazyDependencies
   * @see GradleToolkitExtension.scope
   */
  fun shareLazyDependencies(
    scope: String? = null,
    block: DependencyHandler.() -> Unit,
  )

  @Deprecated("Defining the root `scope` is not necessary", level = DeprecationLevel.HIDDEN)
  override var scope: String?
}
