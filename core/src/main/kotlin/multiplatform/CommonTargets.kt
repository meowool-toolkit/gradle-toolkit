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
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

/**
 * A scope of common target.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class CommonTarget(val extension: KotlinMultiplatformExtension)

/**
 * Enable and configure common target.
 */
inline fun Project.commonTarget(crossinline configure: CommonTarget.() -> Unit = {}) =
  kotlinMultiplatform { configure(CommonTarget(this)) }

/**
 * Configure common target.
 */
inline fun KotlinMultiplatformExtension.common(configure: CommonTarget.() -> Unit = {}) =
  configure(CommonTarget(this))

/**
 * Configure the main source set of common target.
 */
fun CommonTarget.main(configure: KotlinSourceSet.() -> Unit) {
  main.apply(configure)
}

/**
 * Configure the main source set of common target.
 */
fun CommonTarget.test(configure: KotlinSourceSet.() -> Unit) {
  test.apply(configure)
}

val NamedDomainObjectContainer<KotlinSourceSet>.commonMain: KotlinSourceSet
  get() = get("commonMain")

val Project.commonMainSourceSet: KotlinSourceSet
  get() = kotlinMultiplatformExtension.sourceSets.commonMain

val CommonTarget.main: KotlinSourceSet
  get() = extension.sourceSets.commonMain

val NamedDomainObjectContainer<KotlinSourceSet>.commonTest: KotlinSourceSet
  get() = get("commonMain")

val Project.commonTestSourceSet: KotlinSourceSet
  get() = kotlinMultiplatformExtension.sourceSets.commonTest

val CommonTarget.test: KotlinSourceSet
  get() = extension.sourceSets.commonTest
