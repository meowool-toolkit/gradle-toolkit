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
 */
package com.meowbase.plugin.toolkit.ktx

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.register
import java.io.File
import kotlin.reflect.KClass

val Task.inputFileSet: Set<File> get() = inputs.files.files

val Task.outputFileSet: Set<File> get() = outputs.files.files

/**
 * Find the task by [name].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.findTask(name: String): Task? = tasks.findByName(name)

/**
 * Register tasks based on each pair of names and tasks.
 */
fun Project.registerTasks(vararg tasksWithNames: Pair<String, KClass<out Task>>) =
  tasksWithNames.forEach {
    this.tasks.register(it.first, it.second)
  }

/**
 * Skip the corresponding task according to the given [taskNames].
 */
fun Project.skipTasks(vararg taskNames: String) = taskNames.forEach {
  tasks.findByName(it)?.enabled = false
}

/**
 * Skip the corresponding task(s) according to the given [predicate].
 */
fun Project.skipTask(predicate: (Task) -> Boolean) =
  tasks.filter(predicate).forEach { it.enabled = false }

/**
 * Register multiple [tasks], and use their class name as the task name.
 */
fun Project.registerTasks(vararg tasks: KClass<out Task>) = tasks.forEach {
  this.tasks.register(it.java.simpleName, it)
}
