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
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetWithTests.Companion.DEFAULT_TEST_RUN_NAME
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

/**
 * Enable and configure target of jvm target.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.jvmTarget(
  name: String = "jvm",
  configure: KotlinJvmTarget.() -> Unit = {},
) = kotlinMultiplatform { jvm(name, configure) }

/**
 * Configure the main source set of jvm target.
 */
fun KotlinJvmTarget.main(configure: KotlinSourceSet.() -> Unit) {
  main.apply(configure)
}

/**
 * Configure the main source set of jvm target.
 */
fun KotlinJvmTarget.test(configure: KotlinSourceSet.() -> Unit) {
  test.apply(configure)
}

/**
 * Configure the test run task by given [configuration].
 */
fun KotlinJvmTarget.configureTestRunTask(configuration: KotlinJvmTest.() -> Unit) {
  testRuns[DEFAULT_TEST_RUN_NAME].executionTask.configure(configuration)
}

val NamedDomainObjectContainer<KotlinSourceSet>.jvmMain: KotlinSourceSet
  get() = get("jvmMain")

val Project.jvmMainSourceSet: KotlinSourceSet
  get() = mppExtension.sourceSets.jvmMain

val KotlinJvmTarget.main: KotlinSourceSet
  get() = project.jvmMainSourceSet

val NamedDomainObjectContainer<KotlinSourceSet>.jvmTest: KotlinSourceSet
  get() = get("jvmTest")

val Project.jvmTestSourceSet: KotlinSourceSet
  get() = mppExtension.sourceSets.jvmTest

val KotlinJvmTarget.test: KotlinSourceSet
  get() = project.jvmTestSourceSet
