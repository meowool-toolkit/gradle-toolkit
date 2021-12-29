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
@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package com.meowool.gradle.toolkit

import DependencyHandlerToolkit
import com.meowool.gradle.toolkit.internal.GradleToolkitExtensionImpl.Companion.toolkitExtensionImpl
import com.meowool.gradle.toolkit.internal.InternalGradleToolkitApi
import injectDependenciesLogic
import injectProjectLogic
import org.gradle.api.Project
import java.util.concurrent.ConcurrentHashMap

/**
 * The Logical registry holds all shared code logic.
 *
 * Related references, [Dependency injection](https://en.wikipedia.org/wiki/Dependency_injection).
 *
 * @author 凛 (RinOrz)
 */
class LogicRegistry {
  @InternalGradleToolkitApi
  val extraLogics = ConcurrentHashMap<Any, Any>()
  internal val projectLogics = ConcurrentHashMap<Any, Project.() -> Unit>()
  internal val dependenciesLogics = ConcurrentHashMap<Any, DependencyHandlerToolkit.() -> Unit>()

  /**
   * Registers the project block [logic] belonging to the specified [key].
   *
   * And then you can call [injectProjectLogic] in the any required project to inject [logic].
   */
  fun project(key: Any = DefaultProjectKey, logic: Project.() -> Unit) {
    projectLogics.requireNotKey(key)
    projectLogics[key] = logic
  }

  /**
   * Registers the dependencies block [logic] belonging to the specified [key].
   *
   * And then you can call [injectDependenciesLogic] in the any required project to inject [logic].
   */
  fun dependencies(key: Any = DefaultDependenciesKey, logic: DependencyHandlerToolkit.() -> Unit) {
    dependenciesLogics.requireNotKey(key)
    dependenciesLogics[key] = logic
  }

  @InternalGradleToolkitApi companion object {
    const val DefaultProjectKey = "default project logic"
    const val DefaultDependenciesKey = "default dependencies logic"

    val Project.logicRegistry: LogicRegistry
      get() = toolkitExtensionImpl.logicRegistry

    inline fun ConcurrentHashMap<*, *>.requireNotKey(key: Any) =
      require(containsKey(key).not()) { "$key already registered." }
    inline fun notFoundKey(key: Any): Nothing =
      error("Found not the logic related to the $key, please make sure you have registered.")
  }
}
