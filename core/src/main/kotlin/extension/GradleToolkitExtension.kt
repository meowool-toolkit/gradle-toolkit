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
package extension

import importLazyDependencies
import kotlinCompile
import kotlinJvmCompile
import kotlinJvmOptions
import kotlinOptions
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

/**
 * An extension of the gradle toolkit belonging to [project].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
interface GradleToolkitExtension {

  /**
   * Returns the closest project.
   */
  val project: Project

  /**
   * The scope of the [project].
   *
   * This optional value is used in
   * [RootGradleToolkitExtension.shareDependencies]
   * [RootGradleToolkitExtension.shareLazyDependencies]
   * [Project.importLazyDependencies].
   */
  var scope: String?

  /**
   * Additional data storage.
   */
  val data: MutableList<Any>

  /**
   * Imports shared lazy dependencies block by [scope] name.
   *
   * Note that this will use the scope name specified by the current project as much as possible.
   *
   * @see GradleToolkitExtension.scope
   * @see RootGradleToolkitExtension.shareLazyDependencies
   */
  fun importLazyDependencies(scope: String? = null) = project.importLazyDependencies(scope)

  /**
   * Uses given [configuration] to configure kotlin common compile task of this [project].
   */
  fun kotlinCompile(configuration: KotlinCompile<KotlinCommonOptions>.() -> Unit) = project.kotlinCompile(configuration)

  /**
   * Uses given [configuration] to configure kotlin jvm compile task of this [project].
   */
  fun kotlinJvmCompile(configuration: KotlinJvmCompile.() -> Unit) = project.kotlinJvmCompile(configuration)

  /**
   * Configures options for kotlin common compile task of this project with the given [configuration] block.
   */
  fun kotlinOptions(configuration: KotlinCommonOptions.() -> Unit) = project.kotlinOptions(configuration)

  /**
   * Configures options for kotlin jvm compile task of this project with the given [configuration] block.
   */
  fun kotlinJvmOptions(configuration: KotlinCommonOptions.() -> Unit) = project.kotlinJvmOptions(configuration)
}
