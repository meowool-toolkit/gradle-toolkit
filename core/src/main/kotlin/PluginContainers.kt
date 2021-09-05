import org.gradle.api.Plugin
import org.gradle.api.plugins.PluginContainer
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.hasPlugin

/**
 * Kotlin's extension function taking [T] for [org.gradle.kotlin.dsl.apply].
 *
 * @see org.gradle.kotlin.dsl.hasPlugin
 */
inline fun <reified T : Plugin<*>> PluginContainer.apply(): T = apply(T::class)

/**
 * If [T] type plugin is not applied, apply it.
 */
inline fun <reified T : Plugin<*>> PluginContainer.applyIfNotExists() {
  if (hasNotPlugin<T>()) apply(T::class)
}

/**
 * If [id] plugin is not applied, apply it.
 */
fun PluginContainer.applyIfNotExists(id: String) {
  if (hasPlugin(id).not()) apply(id)
}

/**
 * @see org.gradle.kotlin.dsl.hasPlugin
 */
inline fun <reified T: Plugin<*>> PluginContainer.hasPlugin(): Boolean = hasPlugin(T::class)

/**
 * Returns true if no plugin of the [T] is applied.
 */
inline fun <reified T: Plugin<*>> PluginContainer.hasNotPlugin(): Boolean = hasPlugin(T::class).not()