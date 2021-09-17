package com.meowool.gradle.toolkit

/**
 * Used to declare how to search for remote dependencies.
 *
 * @author 凛 (https://github.com/RinOrz)
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