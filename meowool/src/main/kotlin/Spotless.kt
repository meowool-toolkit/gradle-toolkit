@file:Suppress("NOTHING_TO_INLINE")

import com.diffplug.gradle.spotless.JavaExtension
import com.diffplug.gradle.spotless.KotlinExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import com.meowool.sweekt.cast
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.withType

internal val SpotlessExtension.project: Project
  get() = SpotlessExtension::class.java.getDeclaredField("project").apply { isAccessible = true }.get(this).cast()

internal fun SpotlessExtension.whenAvailable(project: Project, block: SpotlessExtension.() -> Unit) {
  project.plugins.withType<JavaPlugin> {
    block()
  }
}

/**
 * When the Kotlin plugin is available, make kotlin spotless with the given [closure].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun SpotlessExtension.kotlinWhenAvailable(project: Project = this.project, closure: Action<KotlinExtension>) {
  whenAvailable(project) { kotlin(closure) }
}

/**
 * When the Java plugin is available, make kotlin spotless with the given [closure].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun SpotlessExtension.javaWhenAvailable(project: Project = this.project, closure: Action<JavaExtension>) {
  whenAvailable(project) { java(closure) }
}