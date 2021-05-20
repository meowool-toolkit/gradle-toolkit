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
@file:Suppress("PackageDirectoryMismatch", "SpellCheckingInspection", "unused")

import dependencies.DependencyNotationAndGroup
import org.gradle.api.Incubating
import org.gradle.kotlin.dsl.IsNotADependency

@Incubating
internal object Kotlin {
  /**
   * Kotlin Standard Library
   *
   * [API reference](https://kotlinlang.org/api/latest/jvm/stdlib/)
   */
  val stdlib = Stdlib

  object Stdlib : DependencyNotationAndGroup(group = "org.jetbrains.kotlin", name = "kotlin-stdlib") {
    @JvmField val jdk7 = "$artifactPrefix-jdk7:_"
    @JvmField val jdk8 = "$artifactPrefix-jdk8:_"
    @JvmField val js = "$artifactPrefix-js:_"
    @JvmField val common = "$artifactPrefix-common:_"
  }

  /**
   * The `kotlin.test` library provides annotations to mark test functions,
   * and a set of utility functions for performing assertions in tests,
   * independently of the test framework being used.
   *
   * [Documentation and API reference](https://kotlinlang.org/api/latest/kotlin.test/)
   */
  val test = Test

  object Test : IsNotADependency {
    private const val artifactPrefix = "org.jetbrains.kotlin:kotlin-test"

    const val annotationsCommon = "$artifactPrefix-annotations-common:_"
    const val common = "$artifactPrefix-common:_"
    const val js = "$artifactPrefix-js:_"
    const val jsRunner = "$artifactPrefix-js-runner:_"

    const val junit = "$artifactPrefix-junit:_"
    const val junit5 = "$artifactPrefix-junit5:_"
    const val testng = "$artifactPrefix-testng:_"
  }
}
