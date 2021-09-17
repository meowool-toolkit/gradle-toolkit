@file:Suppress("UsePropertyAccessSyntax")

import com.meowool.gradle.toolkit.DependencyMapperExtension
import com.meowool.gradle.toolkit.DependencyMapperPlugin
import com.meowool.gradle.toolkit.GradleToolkitExtension
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

/**
 * Configures the dependency mapper based on the given [configuration].
 */
fun Settings.dependencyMapper(configuration: DependencyMapperExtension.() -> Unit = {}) {
  apply<DependencyMapperPlugin>()
  gradle.rootProject { extensions.configure(configuration) }
}

/**
 * Configures the dependency mapper based on the given [configuration].
 */
fun GradleToolkitExtension.dependencyMapper(configuration: DependencyMapperExtension.() -> Unit = {}) {
  rootProject.apply<DependencyMapperPlugin>()
  rootProject.extensions.configure(configuration)
}

/**
 * Returns the gradle toolkit extension of root project.
 */
val GradleToolkitExtension.dependencyMapper: DependencyMapperExtension
  get() {
    rootProject.apply<DependencyMapperPlugin>()
    return rootProject.extensions.getByType()
  }