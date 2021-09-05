import com.meowool.gradle.toolkit.internal.DependencyHandlerDelegate
import com.meowool.gradle.toolkit.internal.LogicRegistry
import com.meowool.gradle.toolkit.internal.LogicRegistry.Companion.DefaultDependenciesKey
import com.meowool.gradle.toolkit.internal.LogicRegistry.Companion.DefaultProjectKey
import com.meowool.gradle.toolkit.internal.LogicRegistry.Companion.logicRegistry
import com.meowool.gradle.toolkit.internal.LogicRegistry.Companion.notFoundKey
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

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
