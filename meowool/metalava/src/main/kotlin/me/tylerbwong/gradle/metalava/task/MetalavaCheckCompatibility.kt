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
package me.tylerbwong.gradle.metalava.task

import me.tylerbwong.gradle.metalava.Module
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec

internal object MetalavaCheckCompatibility : MetalavaTaskContainer() {
  fun registerMetalavaCheckCompatibilityTask(project: Project, extension: MetalavaExtension, module: Module) {
    with(project) {
      val tempFilename = layout.buildDirectory.file("metalava/current.txt").get().asFile.absolutePath
      val generateTempMetalavaSignatureTask = MetalavaSignature.registerMetalavaSignatureTask(
        project = this,
        name = "metalavaGenerateTempSignature",
        description = """
                    Generates a Metalava signature descriptor file in the project build directory for API compatibility
                    checking.
        """.trimIndent(),
        extension = extension,
        module = module,
        filename = tempFilename
      )
      val checkCompatibilityTask = tasks.register("metalavaCheckCompatibility", JavaExec::class.java) {
        group = "verification"
        description = "Checks API compatibility between the code base and the current API."
        mainClass.set("com.android.tools.metalava.Driver")
        classpath(extension.metalavaJarPath?.let { files(it) } ?: getMetalavaClasspath(extension.version))
        dependsOn(generateTempMetalavaSignatureTask)
        // Use temp signature file for incremental Gradle task output
        // If both the current API and temp API have not changed since last run, then
        // consider this task UP-TO-DATE
        inputs.file("api/${extension.filename}")
        outputs.file(tempFilename)

        doFirst {
          // TODO Consolidate flags between tasks
          val hidePackages =
            extension.hiddenPackages.flatMap { listOf("--hide-package", it) }
          val hideAnnotations =
            extension.hiddenAnnotations.flatMap { listOf("--hide-annotation", it) }

          val args: List<String> = listOf(
            "--no-banner",
            "--format=${extension.format}",
            "--source-files", tempFilename,
            "--check-compatibility:api:current", "api/${extension.filename}",
            "--input-kotlin-nulls=${extension.inputKotlinNulls.flagValue}"
          ) + extension.reportWarningsAsErrors.flag("--warnings-as-errors") +
            extension.reportLintsAsErrors.flag("--lints-as-errors") + hidePackages + hideAnnotations

          isIgnoreExitValue = false
          setArgs(args)
        }
      }
      // Projects that apply this plugin should include API compatibility checking as part of their regular checks
      afterEvaluate { tasks.findByName("check")?.dependsOn(checkCompatibilityTask) }
    }
  }
}
