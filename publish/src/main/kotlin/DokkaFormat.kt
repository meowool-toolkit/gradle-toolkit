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
@file:Suppress("unused")

import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask

/**
 * Represents the output format of [Dokka](https://github.com/Kotlin/dokka#output-formats)
 *
 * @author 凛 (https://github.com/RinOrz)
 */
enum class DokkaFormat(val taskName: String) {
  /**
   * HTML format used by default.
   */
  Html("dokkaHtml"),

  /**
   * Looks like JDK's Javadoc, Kotlin classes are translated to Java.
   */
  Javadoc("dokkaJavadoc"),

  /**
   * GitHub flavored markdown.
   */
  Gfm("dokkaGfm"),

  /**
   * Jekyll compatible markdown.
   */
  Jekyll("dokkaJekyll"),
}

/**
 * Configures all dokka task.
 */
fun Project.dokka(configuration: DokkaTask.() -> Unit) {
  afterEvaluate {
    if (!plugins.hasPlugin(DokkaPlugin::class)) apply<DokkaPlugin>()
    tasks.withType(configuration)
  }
}

/**
 * Configures the dokka task of given outputs [format].
 */
fun Project.dokka(format: DokkaFormat = DokkaFormat.Html, configuration: DokkaTask.() -> Unit) {
  afterEvaluate {
    if (!plugins.hasPlugin(DokkaPlugin::class)) apply<DokkaPlugin>()
    (tasks.findByName(format.taskName) as? DokkaTask)?.configuration()
  }
}