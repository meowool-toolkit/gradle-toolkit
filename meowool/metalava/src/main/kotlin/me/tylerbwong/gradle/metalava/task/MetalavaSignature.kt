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
import org.gradle.api.tasks.TaskProvider
import java.io.File

internal object MetalavaSignature : MetalavaTaskContainer() {
  fun registerMetalavaSignatureTask(
    project: Project,
    name: String,
    description: String,
    extension: MetalavaExtension,
    module: Module,
    filename: String = "api/${extension.filename}",
  ): TaskProvider<JavaExec> {
    return with(project) {
      tasks.register(name, JavaExec::class.java) {
        group = "documentation"
        this.description = description
        mainClass.set("com.android.tools.metalava.Driver")
        classpath(extension.metalavaJarPath?.let { files(it) } ?: getMetalavaClasspath(extension.version))
        val sources = file("src")
          .walk()
          .maxDepth(2)
          .onEnter { !it.name.contains("test", ignoreCase = true) }
          .filter { it.isDirectory && (it.name == "java" || it.name == "kotlin") }
          .toList()
        inputs.files(sources)
        outputs.file(filename)

        doFirst {
          val fullClasspath = (module.bootClasspath + module.compileClasspath).joinToString(File.pathSeparator)

          val sourcePaths = listOf("--source-path") + sources.joinToString(File.pathSeparator)
          val hidePackages =
            extension.hiddenPackages.flatMap { listOf("--hide-package", it) }
          val hideAnnotations =
            extension.hiddenAnnotations.flatMap { listOf("--hide-annotation", it) }

          val args: List<String> = listOf(
            "${extension.documentation}",
            "--no-banner",
            "--format=${extension.format}",
            "${extension.signature}", filename,
            "--java-source", "${extension.javaSourceLevel}",
            "--classpath", fullClasspath,
            "--output-kotlin-nulls=${extension.outputKotlinNulls!!.flagValue}",
            "--output-default-values=${extension.outputDefaultValues.flagValue}",
            "--include-signature-version=${extension.includeSignatureVersion.flagValue}"
          ) + sourcePaths + hidePackages + hideAnnotations

          isIgnoreExitValue = true
          setArgs(args)
        }
      }
    }
  }
}
