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
@file:Suppress("SpellCheckingInspection", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

import MappedClassesFactory.Companion.validDependency
import org.gradle.api.Project
import java.io.File
import java.nio.file.Path

private typealias DependencyNotation = String
private typealias DependencyMapped = String

/**
 * The optional configuration of Dependencies Mapper.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
abstract class DependencyMapperExtension(internal val project: Project) {

  /**
   * The name of the generated source file (without extension).
   *
   * At the same time it will affect the root class name.
   */
  var rootClassName: String = "Libs"

  /**
   * The name of generate the jar with mapped dependencies.
   */
  var jarName: String = "deps-mapping.jar"

  /**
   * Capitalizes the first letter of the mapped classes or fields name when [predicate] is true.
   *
   * For example `androidx.compose` will replace to `Androidx.Compose`
   */
  fun capitalizeFirstLetter(predicate: (name: String) -> Boolean = { true }) {
    formatter.capitalizeFirstLetters += predicate
  }

  /**
   * Adds the given [dependencies] to map as classes or fields.
   *
   * For example:
   * ```
   * mapDependencies(
   *   "androidx.compose.ui:ui",
   *   "androidx.appcompat:appcompat",
   *   "androidx.activity:activity-compose",
   * )
   * ```
   */
  fun mapDependencies(vararg dependencies: String) {
    this.dependencies += dependencies.onEach(::validDependency)
  }

  /**
   * Adds the given pair to map the dependencies to the specified fields.
   *
   * For example:
   * ```
   * mapDependencies(
   *   "androidx.compose.ui:ui" to "Compose.Ui",
   *   "androidx.appcompat:appcompat" to "Appcompat.B",
   * )
   * ```
   *
   * @param mappingDependencies The first parameter of pairs is the dependencies, and the second is the
   *   mapped field paths.
   */
  fun mapDependencies(vararg mappingDependencies: Pair<DependencyNotation, DependencyMapped>) {
    this.mappedDependencies += mappingDependencies.onEach { validDependency(it.first) }
  }

  /**
   * Configures remote dependencies via [configuration].
   */
  fun remoteDependencies(configuration: RemoteDependencies.() -> Unit) {
    if (remoteDependencies == null) remoteDependencies = RemoteDependencies()
    remoteDependencies!!.apply(configuration)
  }

  /**
   * Applies the given [transformation] to transform the mapping target of the notation.
   *
   * ```
   * (Notation)                |    (Mapped field)
   * org.apache.cxf:cxf-api    |    Apache.Cxf.Api
   * org.mockito:android       |    Mockito.Android
   * com.google:guava          |    Guava
   * -------------------------------------------------
   * transformPath {
   *   it.removePrefix("org.")
   *     .removePrefix("com.google")
   * }
   * ```
   */
  fun transformNotation(transformation: (String) -> String) {
    formatter.notationReplacers += transformation
  }

  /**
   * Applies the given [transformation] to transform the generated name of classes or field.
   *
   * Note that each part of the path like `com.squareup.okio` is a name like `com` and `squareup` and `okio`.
   *
   * ```
   * transformName {
   *   when(it) {
   *     "androidx" -> "AndroidX"
   *     "activity" -> "Act"
   *     else -> it.replace("jetbrains", "jb")
   *   }
   * }
   * ```
   */
  fun transformName(transformation: (String) -> String) {
    formatter.nameReplacers += transformation
  }

  /**
   * Changes the path of output the generated jar with mapped dependencies to the specified [directory].
   *
   * Default, the jar file write out to the root project.
   *
   * @see jarName
   */
  fun outputTo(directory: File) {
    outputFile = directory.resolve(jarName)
  }

  /**
   * Changes the path of output the generated jar with mapped dependencies to the specified [directory].
   *
   * Default, the jar file write out to the root project.
   *
   * @see jarName
   */
  fun outputTo(directory: Path) = outputTo(directory.toFile())

  /**
   * When the predicate returns `true`, remapping the dependencies.
   *
   * By default, only when a new dependency ([mapDependencies], [RemoteDependencies.keywords],
   * [RemoteDependencies.groups], [RemoteDependencies.starts]) is manually added, will the mapped dependency jar
   * be regenerated. Please note that the code block change like of [transformName] or [transformNotation] will not be
   * recorded. In this case, clean the build directory of project manually and synchronize gradle.
   *
   * @see alwaysUpdate
   */
  fun updateWhen(predicate: (Project) -> Boolean) {
    needUpdate = predicate
  }

  /**
   * Remappings dependencies whenever gradle sync.
   *
   * By default, only when a new dependency ([mapDependencies], [RemoteDependencies.keywords],
   * [RemoteDependencies.groups], [RemoteDependencies.starts]) is manually added, will the mapped dependency jar
   * be regenerated. Please note that the code block change like of [transformName] or [transformNotation] will not be
   * recorded. In this case, clean the build directory of project manually and synchronize gradle.
   *
   * @see updateWhen
   */
  fun alwaysUpdate() {
    needUpdate = { true }
  }


  ///////////////////////////////////////////////////////////////////////////
  ////                           Internal APIs                           ////
  ///////////////////////////////////////////////////////////////////////////

  internal val formatter = DepFormatter()
  internal val dependencies = mutableListOf<String>()
  internal val mappedDependencies = mutableMapOf<DependencyNotation, DependencyMapped>()
  internal var remoteDependencies: RemoteDependencies? = null
  internal var needUpdate: ((Project) -> Boolean)? = null
  internal var outputFile: File = project.file(jarName)
}