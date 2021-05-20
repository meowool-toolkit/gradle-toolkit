/*
 * Copyright (c) 2019. The Meowool Organization Open Source Project
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

import org.gradle.api.Incubating
import org.gradle.kotlin.dsl.IsNotADependency

@Incubating
internal object JakeWharton {

  /**
   * A logger with a small, extensible API which provides utility on top of Android's normal Log class.
   *
   * GitHub page: [JakeWharton/timber](https://github.com/JakeWharton/timber)
   */
  const val timber = "com.jakewharton.timber:timber:_"

  /**
   * A Kotlin DSL and Java/Kotlin builder API for constructing HTML-like tables which can be rendered to text.
   *
   * GitHub page: [JakeWharton/picnic](https://github.com/JakeWharton/picnic)
   */
  const val picnic = "com.jakewharton.picnic:picnic:_"

  /**
   * A Kotlin compiler plugin which brings Kotlin/JS's `unsafeCast` to Kotlin/JVM.
   *
   * GitHub page: [JakeWharton/confundus](https://github.com/JakeWharton/confundus)
   */
  const val confundusGradlePlugin = "com.jakewharton.confundus:confundus-gradle:_"

  /**
   * A time-traveling bytecode rewriter which adds future APIs to `android.jar` which can be desugared to all API levels by D8 and R8.
   *
   * GitHub page: [JakeWharton/wormhole](https://github.com/JakeWharton/wormhole)
   */
  const val wormholeGradlePlugin = "com.jakewharton.wormhole:wormhole-gradle:_"

  /**
   * A type-safe HTTP client for Android and the JVM
   *
   * GitHub page: [square/retrofit](https://github.com/square/retrofit)
   */
  val retrofit2 = Retrofit2

  object Retrofit2 : IsNotADependency {
    private const val artifactPrefix = "com.jakewharton.retrofit:retrofit2"

    val converter = Converter

    object Converter : IsNotADependency {

      /**
       * A Retrofit 2 `Converter.Factory` for [Kotlin serialization](https://github.com/Kotlin/kotlinx.serialization/).
       *
       * GitHub page: [JakeWharton/retrofit2-kotlinx-serialization-converter](https://github.com/JakeWharton/retrofit2-kotlinx-serialization-converter)
       */
      const val kotlinxSerialization = "$artifactPrefix-kotlinx-serialization-converter:_"
    }
  }

  val moshi = Moshi

  object Moshi : IsNotADependency {

    /**
     * Shimo is a `JsonAdapter.Factory` for [Square.moshi] which randomizes the order of keys
     * when serializing objects to JSON and when deserializing objects from JSON.
     *
     * GitHub page: [JakeWharton/shimo](https://github.com/JakeWharton/shimo)
     */
    const val shimo = "com.jakewharton.moshi:shimo:_"
  }
}
