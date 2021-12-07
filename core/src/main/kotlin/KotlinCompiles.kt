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
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

/**
 * Uses given [configuration] to configure kotlin common compile task of this project.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.kotlinCompile(configuration: KotlinCompile<KotlinCommonOptions>.() -> Unit) {
  kotlinMultiplatformExtensionOrNull?.targets?.all {
    compilations.all {
      compileKotlinTask.apply(configuration)
    }
  }
  tasks.withType<KotlinCompile<KotlinCommonOptions>>().all(configuration)
}

/**
 * Uses given [configuration] to configure kotlin jvm compile task of this project.
 */
fun Project.kotlinJvmCompile(configuration: KotlinJvmCompile.() -> Unit) {
  kotlinMultiplatformExtensionOrNull?.targets?.all {
    if (this is KotlinJvmTarget) compilations.all {
      compileKotlinTask.apply(configuration)
    }
  }
  tasks.withType<KotlinJvmCompile>().all(configuration)
}

/**
 * Configures options for kotlin common compile task of this project with the given [configuration] block.
 */
fun Project.kotlinOptions(configuration: KotlinCommonOptions.() -> Unit) {
  kotlinCompile { kotlinOptions(configuration) }
}

/**
 * Configures options for kotlin jvm compile task of this project with the given [configuration] block.
 */
fun Project.kotlinJvmOptions(configuration: KotlinJvmOptions.() -> Unit) {
  kotlinJvmCompile { kotlinJvmOptions(configuration) }
}

/**
 * Configures options for kotlin jvm compile task with the given [configuration] block.
 */
fun KotlinCompile<*>.kotlinJvmOptions(configuration: KotlinJvmOptions.() -> Unit) = kotlinOptions {
  if (this is KotlinJvmOptions) configuration()
}

/**
 * If compile task belongs to the default variant, execute the given [action] block.
 */
inline fun <T : KotlinCompile<*>> T.onDefaultVariant(action: T.() -> Unit) {
  if (name == "compileKotlin") action()
}

/**
 * If compile task belongs to the test variant, execute the given [action] block.
 */
inline fun <T : KotlinCompile<*>> T.onTestVariant(action: T.() -> Unit) {
  if (name == "compileTestKotlin") action()
}
