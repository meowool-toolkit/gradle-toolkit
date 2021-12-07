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
@file:Suppress("SpellCheckingInspection")

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

/**
 * Sets the Kotlin explicit api mode of this project.
 *
 * For more details, see [doc](https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors)
 */
fun Project.kotlinExplicitApi(mode: ExplicitApiMode = ExplicitApiMode.Strict) = kotlinOptions {
  addFreeCompilerArgs(mode.toCompilerArg())
}

/**
 * Sets the Kotlin explicit api mode for default variant of this project.
 *
 * For more details, see [doc](https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors)
 */
fun Project.kotlinDefaultVariantExplicitApi(mode: ExplicitApiMode = ExplicitApiMode.Strict) = kotlinCompile {
  onDefaultVariant { addFreeCompilerArgs(mode.toCompilerArg()) }
}

/**
 * Sets the Kotlin explicit api mode for test variant of this project.
 *
 * For more details, see [doc](https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors)
 */
fun Project.kotlinTestVariantExplicitApi(mode: ExplicitApiMode = ExplicitApiMode.Strict) = kotlinCompile {
  onTestVariant { addFreeCompilerArgs(mode.toCompilerArg()) }
}
