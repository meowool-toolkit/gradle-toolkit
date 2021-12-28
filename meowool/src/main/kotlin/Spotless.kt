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
  project.plugins.withType<JavaPlugin>().configureEach { block() }
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
