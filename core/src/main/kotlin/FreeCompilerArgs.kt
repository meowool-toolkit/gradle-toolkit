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
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * [See](https://github.com/JetBrains/kotlin/blob/master/compiler/cli/cli-common/src/org/jetbrains/kotlin/cli/common/arguments/CommonCompilerArguments.kt)
 *
 * @author 凛 (RinOrz)
 */
fun KotlinCommonOptions.addFreeCompilerArgs(vararg args: String) {
  freeCompilerArgs = (freeCompilerArgs + args).distinct()
}

/**
 * [See](https://github.com/JetBrains/kotlin/blob/master/compiler/cli/cli-common/src/org/jetbrains/kotlin/cli/common/arguments/CommonCompilerArguments.kt)
 */
fun KotlinCommonOptions.addFreeCompilerArgs(args: Iterable<String>) {
  freeCompilerArgs = (freeCompilerArgs + args).distinct()
}

/**
 * [See](https://github.com/JetBrains/kotlin/blob/master/compiler/cli/cli-common/src/org/jetbrains/kotlin/cli/common/arguments/CommonCompilerArguments.kt)
 */
fun KotlinCompile.addFreeCompilerArgs(vararg args: String) = kotlinOptions {
  freeCompilerArgs = (freeCompilerArgs + args).distinct()
}

/**
 * [See](https://github.com/JetBrains/kotlin/blob/master/compiler/cli/cli-common/src/org/jetbrains/kotlin/cli/common/arguments/CommonCompilerArguments.kt)
 */
fun KotlinCompile.addFreeCompilerArgs(args: Iterable<String>) = kotlinOptions {
  freeCompilerArgs = (freeCompilerArgs + args).distinct()
}

/**
 * [See](https://github.com/JetBrains/kotlin/blob/master/compiler/cli/cli-common/src/org/jetbrains/kotlin/cli/common/arguments/CommonCompilerArguments.kt)
 */
fun Project.addFreeCompilerArgs(vararg args: String) = kotlinOptions { addFreeCompilerArgs(*args) }

/**
 * [See](https://github.com/JetBrains/kotlin/blob/master/compiler/cli/cli-common/src/org/jetbrains/kotlin/cli/common/arguments/CommonCompilerArguments.kt)
 */
fun Project.addFreeCompilerArgs(args: Iterable<String>) = kotlinOptions { addFreeCompilerArgs(args) }
