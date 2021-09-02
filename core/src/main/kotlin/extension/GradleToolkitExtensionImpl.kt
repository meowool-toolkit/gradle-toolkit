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

import org.gradle.api.Project

internal open class GradleToolkitExtensionImpl(override val rootProject: Project) : GradleToolkitExtension {
  val logicRegistry = LogicRegistry()
  override val allprojects: Set<Project>
    get() = rootProject.allprojects
  override val subprojects: Set<Project>
    get() = rootProject.subprojects

  override fun allprojects(afterEvaluate: Boolean, filter: Project.() -> Boolean, action: Project.() -> Unit) =
    rootProject.allprojects {
      if (filter(this)) {
        if (afterEvaluate) afterEvaluate(action) else action()
      }
    }

  override fun subprojects(afterEvaluate: Boolean, filter: Project.() -> Boolean, action: Project.() -> Unit) =
    rootProject.subprojects {
      if (filter(this)) {
        if (afterEvaluate) afterEvaluate(action) else action()
      }
    }

  override fun registerLogic(registry: LogicRegistry.() -> Unit) {
    logicRegistry.apply(registry)
  }
}