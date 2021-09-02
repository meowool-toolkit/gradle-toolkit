@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

import LogicRegistry.Companion.DefaultDependenciesKey
import LogicRegistry.Companion.DefaultProjectKey
import LogicRegistry.Companion.notFoundKey
import annotation.InternalGradleToolkitApi
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import java.util.concurrent.ConcurrentHashMap

/**
 * The Logical registry holds all shared code logic.
 *
 * Related references, [Dependency injection](https://en.wikipedia.org/wiki/Dependency_injection).
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class LogicRegistry {
  @InternalGradleToolkitApi
  val extraLogics = ConcurrentHashMap<Any, Any>()
  internal val projectLogics = ConcurrentHashMap<Any, Project.() -> Unit>()
  internal val dependenciesLogics = ConcurrentHashMap<Any, DependencyHandlerDelegate.() -> Unit>()

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
  fun dependencies(key: Any = DefaultDependenciesKey, logic: DependencyHandlerDelegate.() -> Unit) {
    dependenciesLogics.requireNotKey(key)
    dependenciesLogics[key] = logic
  }

  @InternalGradleToolkitApi companion object {
    const val DefaultProjectKey = "default project logic"
    const val DefaultDependenciesKey = "default dependencies logic"
    inline fun ConcurrentHashMap<*, *>.requireNotKey(key: Any) =
      require(containsKey(key).not()) { "$key already registered." }
    inline fun notFoundKey(key: Any): Nothing =
      error("Found not the logic related to the $key, please make sure you have registered.")
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
fun Project.injectDependenciesLogic(key: Any = DefaultDependenciesKey) = dependencies {
  val logic = logicRegistry.dependenciesLogics[key] ?: notFoundKey(key)
  logic(DependencyHandlerDelegate(project, this))
}
