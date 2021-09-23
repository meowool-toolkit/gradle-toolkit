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

 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
package me.tylerbwong.gradle.metalava.plugin

import me.tylerbwong.gradle.metalava.Module.Companion.module
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import me.tylerbwong.gradle.metalava.task.MetalavaCheckCompatibility
import me.tylerbwong.gradle.metalava.task.MetalavaSignature
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete

class MetalavaPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      val extension = extensions.create("metalava", MetalavaExtension::class.java)
      afterEvaluate {
        val currentModule = module(extension) ?: return@afterEvaluate
        MetalavaSignature.registerMetalavaSignatureTask(
          project = this,
          name = "metalavaGenerateSignature",
          description = "Generates a Metalava signature descriptor file.",
          extension = extension,
          module = currentModule
        )
        MetalavaCheckCompatibility.registerMetalavaCheckCompatibilityTask(
          project = this,
          extension = extension,
          module = currentModule
        )
        tasks.register("metalavaCleanup", Delete::class.java) {
          group = "cleanup"
          delete("api")
        }
      }
    }
  }
}
