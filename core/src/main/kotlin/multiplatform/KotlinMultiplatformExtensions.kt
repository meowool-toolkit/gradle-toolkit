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
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Configure the [KotlinMultiplatformExtension] of this project.
 *
 * @author 凛 (RinOrz)
 */
fun Project.kotlinMultiplatform(configuration: KotlinMultiplatformExtension.() -> Unit) {
  project.plugins.applyIfNotExists(kotlinMultiplatformPluginId)
  project.extensions.configure(configuration)
}

internal fun Project.kotlinMultiplatformWhenAvailable(configuration: KotlinMultiplatformExtension.() -> Unit) {
  project.plugins.withId(kotlinMultiplatformPluginId) {
    project.extensions.configure(configuration)
  }
}

internal inline val Project.kotlinMultiplatformExtensionOrNull: KotlinMultiplatformExtension?
  get() = extensions.findByType()

internal inline val Project.kotlinMultiplatformExtension: KotlinMultiplatformExtension
  get() = kotlinMultiplatformExtensionOrNull ?: error("Please apply the `kotlin-multiplatform` plugin first.")

internal const val kotlinMultiplatformPluginId = "kotlin-multiplatform"
