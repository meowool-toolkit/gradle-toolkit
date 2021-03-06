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
import com.meowool.gradle.toolkit.LogicRegistry
import com.meowool.gradle.toolkit.LogicRegistry.Companion.DefaultDependenciesKey
import com.meowool.gradle.toolkit.LogicRegistry.Companion.DefaultProjectKey
import com.meowool.gradle.toolkit.LogicRegistry.Companion.logicRegistry
import com.meowool.gradle.toolkit.LogicRegistry.Companion.notFoundKey
import com.meowool.gradle.toolkit.internal.GradleToolkitExtensionImpl.Companion.toolkitExtension
import com.meowool.sweekt.runOrNull
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.DependencyHandlerScope

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
fun Project.registerLogic(registry: LogicRegistry.() -> Unit) {
  require(this == rootProject) { "You can only register logic in the settings.gradle(.kts) or build.gradle(.kts) of root project." }
  toolkitExtension.registerLogic(registry)

  allprojects {
    afterEvaluate {
      runOrNull { injectProjectLogic(ignoreUnregistered = true) }
      runOrNull { injectDependenciesLogic(ignoreUnregistered = true) }
    }
  }
}

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
fun Settings.registerLogic(registry: LogicRegistry.() -> Unit) {
  gradleToolkit {
    registerLogic(registry)
  }
}

/**
 * Injects the shared logic of project block registered in [LogicRegistry] into this project.
 *
 * @param key The key of the registered logic.
 * @see LogicRegistry.project
 */
fun Project.injectProjectLogic(key: Any = DefaultProjectKey, ignoreUnregistered: Boolean = false) {
  val logic = logicRegistry.projectLogics[key]
  if (ignoreUnregistered && logic == null) return
  logic ?: notFoundKey(key)
  logic(this)
}

/**
 * Injects the shared logic of dependencies block registered in [LogicRegistry] into this project.
 *
 * @param key The key of the registered logic.
 * @see LogicRegistry.dependencies
 */
fun Project.injectDependenciesLogic(key: Any = DefaultDependenciesKey, ignoreUnregistered: Boolean = false) {
  val logic = logicRegistry.dependenciesLogics[key]
  if (ignoreUnregistered && logic == null) return
  logic ?: notFoundKey(key)
  logic(DependencyHandlerToolkit(project, DependencyHandlerScope.of(dependencies)))
}
