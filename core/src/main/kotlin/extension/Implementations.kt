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

import MainScope
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler

internal open class GradleToolkitExtensionImpl(override val project: Project) : GradleToolkitExtension {
  override val data: MutableList<Any> = mutableListOf()

  override var scope: String? = null
}

internal class RootGradleToolkitExtensionImpl(override val rootProject: Project) :
  GradleToolkitExtensionImpl(rootProject), RootGradleToolkitExtension {
  val sharedDependencies = hashMapOf<String, DependencyHandler.() -> Unit>()
  val sharedLazyDependencies = hashMapOf<String, DependencyHandler.() -> Unit>()
  override val allprojects: Set<Project>
    get() = rootProject.allprojects
  override val subprojects: Set<Project>
    get() = rootProject.subprojects

  override fun allprojects(action: Project.() -> Unit) = rootProject.allprojects(action)
  override fun subprojects(action: Project.() -> Unit) = rootProject.subprojects(action)

  override fun shareDependencies(scope: String?, block: DependencyHandler.() -> Unit) {
    sharedDependencies[scope ?: MainScope] = block
  }

  override fun shareLazyDependencies(scope: String?, block: DependencyHandler.() -> Unit) {
    sharedLazyDependencies[scope ?: MainScope] = block
  }
}
