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
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Enable all language features by given [names].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.enableLanguageFeatures(vararg names: String) =
  extensions.findByType<KotlinMultiplatformExtension>()?.enableLanguageFeatures(*names)

/**
 * Enable all language features by given [names].
 */
fun Project.enableLanguageFeatures(names: List<String>) =
  enableLanguageFeatures(*names.toTypedArray())

/**
 * Enable all language features by given [names].
 */
fun KotlinMultiplatformExtension.enableLanguageFeatures(vararg names: String) = sourceSets.all {
  languageSettings {
    names.forEach(::enableLanguageFeature)
  }
}

/**
 * Enable all language features by given [names].
 */
fun KotlinMultiplatformExtension.enableLanguageFeatures(names: List<String>) =
  enableLanguageFeatures(*names.toTypedArray())