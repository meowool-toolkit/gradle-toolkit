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

import ApplySourceScope
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * All gradle-dsl-x available extensions.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
interface GradleDslExtension {

  /**
   * Returns the closest project.
   */
  val project: Project

  /**
   * Set or get the scope of the current project.
   *
   * Note that the value is optional.
   *
   * @see RootGradleDslExtension.shareDependencies
   */
  var scope: String?

  /**
   * Additional data storage.
   */
  val data: MutableList<Any>

  /**
   * Use given [configuration] to configure kotlin compile task.
   *
   * Note that the task name as `compileKotlin`.
   */
  fun configureKotlinCompile(configuration: KotlinCompile.() -> Unit)

  /**
   * Use given [configuration] to configure kotlin test compile task.
   *
   * Note that the task name as `compileTestKotlin`.
   */
  fun configureKotlinTestCompile(configuration: KotlinCompile.() -> Unit)

  /**
   * Use given [configuration] to configure all kotlin compile task.
   *
   * Tasks with name compileKotlin and compileTestKotlin.
   */
  fun configureAllKotlinCompile(configuration: KotlinCompile.() -> Unit)

  fun kotlinExplicitApi(
    mode: ExplicitApiMode = ExplicitApiMode.Strict,
    applyScope: ApplySourceScope = ApplySourceScope.Default
  )
}
