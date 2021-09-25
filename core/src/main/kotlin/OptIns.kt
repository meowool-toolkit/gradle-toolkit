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
@file:Suppress("SpellCheckingInspection")

import com.meowool.sweekt.iteration.isEmpty
import org.gradle.api.Project

/**
 * Uses all given annotation classes [annotationNames] and suppress their experimental warning.
 *
 * For more details, see [Opt-in](https://kotlinlang.org/docs/opt-in-requirements.html)
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.optIn(vararg annotationNames: String) = optIn(annotationNames.toList())

/**
 * Uses all given annotation classes [annotationNames] and suppress their experimental warning.
 *
 * For more details, see [Opt-in](https://kotlinlang.org/docs/opt-in-requirements.html)
 */
fun Project.optIn(annotationNames: Iterable<String>) {
  if (annotationNames.isEmpty()) return
  kotlinMultiplatformExtensionOrNull?.sourceSets?.all {
    languageSettings { annotationNames.forEach(::optIn) }
  }
  addFreeCompilerArgs(annotationNames.map { "-Xopt-in=$it" })
}
