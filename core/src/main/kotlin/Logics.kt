import com.meowool.gradle.toolkit.internal.GradleToolkitExtensionImpl.Companion.toolkitExtension
import com.meowool.gradle.toolkit.LogicRegistry
import com.meowool.gradle.toolkit.LogicRegistry.Companion.DefaultDependenciesKey
import com.meowool.gradle.toolkit.LogicRegistry.Companion.DefaultProjectKey
import com.meowool.gradle.toolkit.LogicRegistry.Companion.logicRegistry
import com.meowool.gradle.toolkit.LogicRegistry.Companion.notFoundKey
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
 * // Then other projects, inject with
 * // `injectProjectLogic()`, `injectDependenciesLogic()`, `injectDependenciesLogic(key = 1)`
 * ```
 */
fun Project.registerLogic(registry: LogicRegistry.() -> Unit) {
  require (this == rootProject) { "You can only register logic in the settings.gradle(.kts) or build.gradle(.kts) of root project." }
  toolkitExtension.registerLogic(registry)
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
 * // Then other projects, inject with
 * // `injectProjectLogic()`, `injectDependenciesLogic()`, `injectDependenciesLogic(key = 1)`
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
fun Project.injectProjectLogic(key: Any = DefaultProjectKey) {
  val logic = logicRegistry.projectLogics[key] ?: notFoundKey(key)
  logic(this)
}

/**
 * Injects the shared logic of dependencies block registered in [LogicRegistry] into this project.
 *
 * @param key The key of the registered logic.
 * @see LogicRegistry.dependencies
 */
fun Project.injectDependenciesLogic(key: Any = DefaultDependenciesKey) {
  val logic = logicRegistry.dependenciesLogics[key] ?: notFoundKey(key)
  logic(DependencyHandlerToolkit(project, DependencyHandlerScope.of(dependencies)))
}
