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
@file:Suppress("SpellCheckingInspection")

import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Uses all given annotation classes [annotationNames] and suppress their experimental warning.
 *
 * For more details, see [Opt-in](https://kotlinlang.org/docs/opt-in-requirements.html)
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.optIn(vararg annotationNames: String) {
  addFreeCompilerArgs(annotationNames.map { "-Xopt-in=$it" })
  extensions.findByType<KotlinMultiplatformExtension>()?.sourceSets?.all {
    languageSettings {
      // TODO Remove since 1.5.30
      annotationNames.forEach(::useExperimentalAnnotation)
    }
  }
}

/**
 * Uses all given annotation classes [annotationNames] and suppress their experimental warning.
 *
 * For more details, see [Opt-in](https://kotlinlang.org/docs/opt-in-requirements.html)
 */
fun Project.optIn(annotationNames: Iterable<String>) {
  addFreeCompilerArgs(annotationNames.map { "-Xopt-in=$it" })
  extensions.findByType<KotlinMultiplatformExtension>()?.sourceSets?.all {
    languageSettings {
      // TODO Remove since 1.5.30
      annotationNames.forEach(::useExperimentalAnnotation)
    }
  }
}
