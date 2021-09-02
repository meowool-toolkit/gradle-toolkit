@file:Suppress("UsePropertyAccessSyntax")

import com.meowool.sweekt.cast
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType

/**
 * A plugin that can map dependencies to classes or fields.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class DependencyMapper : Plugin<Any> {
  override fun apply(target: Any) {
    when (target) {
      is Settings -> target.bootstrap()
      is Project -> target.bootstrap()
    }
  }

  private fun Settings.bootstrap() {
    gradle.rootProject { bootstrap() }
  }

  private fun Project.bootstrap() {
    extensions.addIfNotExists(DependencyMapperExtension::class, "dependencyMapper") {
      DependencyMapperExtensionImpl(this)
    }
    afterEvaluate {
      extensions.findByType<DependencyMapperExtension>()
        .cast<DependencyMapperExtensionImpl>()
        .mapping()
    }
  }
}

/**
 * Configures the dependency mapper based on the given [configuration].
 */
fun Settings.dependencyMapper(configuration: DependencyMapperExtension.() -> Unit = {}) {
  apply<DependencyMapper>()
  gradle.rootProject { extensions.configure(configuration) }
}

/**
 * Configures the dependency mapper based on the given [configuration].
 */
fun GradleToolkitExtension.dependencyMapper(configuration: DependencyMapperExtension.() -> Unit = {}) {
  rootProject.apply<DependencyMapper>()
  rootProject.extensions.configure(configuration)
}

/**
 * Returns the gradle toolkit extension of root project.
 */
val GradleToolkitExtension.dependencyMapper: DependencyMapperExtension
  get() {
    rootProject.apply<DependencyMapper>()
    return rootProject.extensions.getByType()
  }