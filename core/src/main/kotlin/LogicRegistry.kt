@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package com.meowool.gradle.toolkit

import DependencyHandlerToolkit
import com.meowool.gradle.toolkit.internal.GradleToolkitExtensionImpl.Companion.toolkitExtensionImpl
import com.meowool.gradle.toolkit.internal.InternalGradleToolkitApi
import injectProjectLogic
import org.gradle.api.Project
import injectDependenciesLogic
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