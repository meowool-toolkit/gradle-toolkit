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
package com.meowool.gradle.toolkit

import java.time.Duration

/**
 * Used to declare how to search for remote dependencies.
 *
 * @author 凛 (RinOrz)
 */
interface SearchDeclaration<Result> {

  /**
   * Adds [Maven Central Repository](https://search.maven.org/) as a source of dependencies searches.
   */
  fun fromMavenCentral()

  /**
   * Adds [Google's Maven Repository](https://maven.google.com/) as a source of dependencies searches.
   */
  fun fromGoogle()

  /**
   * Adds [Gradle Portal](https://plugins.gradle.org/) as a source of dependencies searches.
   */
  fun fromGradlePluginPortal()

  /**
   * Adds [MvnRepository](https://mvnrepository.com/) as a source of dependencies searches.
   *
   * @param fetchExactly Some scala dependencies have special version. By default, this source will not resolve them.
   *   Only when the value is `true` will it spend more time to resolve them. For example,
   *   `org.scala-lang:scala3-library`, which is actually `org.scala-lang:scala3-library_3`. For more details, see
   *   [Scala docs](https://www.scala-sbt.org/1.x/docs/Library-Dependencies.html#Getting+the+right+Scala+version+with)
   */
  fun fromMvnRepository(fetchExactly: Boolean = false)

  /**
   * Sets a minimum count for search results.
   *
   * @param minCount The minimum expected count of the searched results.
   * @param retryIfMissing If the value is `true`, try again when the count of searched results is less than [minCount],
   *   otherwise throw an exception.
   * @param retryTimeout Retry timeout when [retryIfMissing] is true.
   */
  fun requireResultAtLeast(
    minCount: Int,
    retryIfMissing: Boolean = true,
    retryTimeout: Duration = Duration.ofMinutes(1)
  )

  /**
   * Sets the filter [predicate] to filter search results.
   *
   * @param predicate If the predicate is `false`, dependency will be excluded.
   */
  fun filter(predicate: (Result) -> Boolean)

  /**
   * Sets the filter [predicate] to filter search results.
   *
   * @param predicate If the predicate is `true`, dependency will be excluded.
   */
  fun filterNot(predicate: (Result) -> Boolean) = filter { predicate(it).not() }
}
