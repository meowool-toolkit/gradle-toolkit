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
inline fun <reified T : Plugin<*>> PluginContainer.hasPlugin(): Boolean = hasPlugin(T::class)

/**
 * Returns true if no plugin of the [T] is applied.
 */
inline fun <reified T : Plugin<*>> PluginContainer.hasNotPlugin(): Boolean = hasPlugin(T::class).not()
