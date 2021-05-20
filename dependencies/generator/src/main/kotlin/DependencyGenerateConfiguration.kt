/*
 * Copyright (c) $\YEAR. The Meowool Organization Open Source Project
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
import org.gradle.api.Project
import java.io.File
import java.io.OutputStream
import java.io.PrintStream

/**
 * The optional configuration of Generate dependencies.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
abstract class DependencyGenerateConfiguration {

  /**
   * When the group id last name is the same as the artifact beginning,
   * reduced the class create.
   *
   * E.g.
   * `androidx.compose.ui:ui`
   * `androidx.compose.ui:ui-tooling`
   * will create:
   * ```
   * class AndroidX {
   *   class Compose {
   *     object Ui
   *     object UiTooling
   *   }
   * }
   * ```
   */
  var simplifyClassName: Boolean = true

  /**
   * The name of the generated source file (without extension).
   *
   * At the same time it will affect the root class name.
   */
  var fileName: String = "Libs"

  /**
   * Capitalize the first letter of the generated classes name when [predicate] is true.
   *
   * For example `androidx.compose` will replaced `Androidx.Compose`
   */
  abstract fun upperCamelCase(predicate: (String) -> Boolean = { true })

  /**
   * Generate dependencies based on the given [declaration].
   *
   * Note that one line represents one dependency declaration.
   *
   * ```
   * declareDependencies(
   *   "androidx.compose.ui:ui",
   *   "androidx.appcompat:appcompat",
   *   "androidx.activity:activity-compose [name=Compose]",
   * )
   * ```
   */
  abstract fun declareDependencies(vararg declaration: String)

  /**
   * Generate dependencies based on the given [declaration] files content.
   *
   * @see declareDependencies[String]
   */
  abstract fun declareDependencies(vararg declaration: File)

  /**
   * Get dependencies from the maven central repository.
   * Optional parameters: [all|group]
   *
   * Note that one line represents one dependency search declaration.
   *
   * ```
   * declareMavenCentralDependencies(
   *   // Search all keywords related
   *   "org.jetbrains.kotlin [all]",
   *   // Search for those that match the group
   *   "com.squareup",
   *   "com.squareup [group]",
   * )
   * ```
   */
  abstract fun declareMavenCentralDependencies(vararg keywords: String)

  /**
   * Get dependencies from the maven central repository.
   *
   * @see declareMavenCentralDependencies[String]
   */
  abstract fun declareMavenCentralDependencies(vararg searchDeclaration: File)

  /**
   * Replace the dependencies group id according to the [rule].
   *
   * Note that one line represents one dependency replace rule declaration.
   *
   * ```
   * replaceGroups(
   *   """
   *     androidx.activity -> androidx
   *     androidx.appcompat -> androidx
   *     androidx.compose.ui -> androidx.compose
   *   """.trimIndent()
   * )
   * ```
   */
  abstract fun replaceGroups(vararg rule: String)

  /**
   * Replace the dependencies group id according to the [rule] files content.
   *
   * @see replaceGroups[String]
   */
  abstract fun replaceGroups(vararg rule: File)

  /**
   * Apply the given [transformation] to generated classes name.
   *
   * ```
   * classNameTransform {
   *   when(it) {
   *     "androidx" -> "AndroidX"
   *     "activity" -> "Act"
   *     else -> it
   *   }
   * }
   * ```
   */
  abstract fun classNameTransform(transformation: (String) -> String)

  /**
   * Apply the given [transformation] to generated classes type.
   *
   * ```
   * // com.a.b -> a.b
   * // io.github.xyz -> xyz
   * classTypeTransform {
   *   it.removePrefix("com.")
   *     .removePrefix("io.github.")
   * }
   * ```
   */
  abstract fun classParentTransform(transformation: (String) -> String)

  /**
   * Set the file write out to the build directory of current project.
   *
   * Default, the file write out to the `buildSrc` of root project.
   *
   * @see outputTo[File]
   * @see outputTo[Appendable]
   * @see outputTo[OutputStream]
   */
  abstract fun outputToProject()

  /**
   * Write the generated code to given [appendable], like [PrintStream].
   *
   * Default, the file write out to the `buildSrc` of root project.
   *
   * @see outputToProject
   * @see outputTo[File]
   * @see outputTo[OutputStream]
   */
  abstract fun outputTo(appendable: Appendable)

  /**
   * Write the generated code to given [outputStream], like [OutputStream].
   *
   * Default, the file write out to the `buildSrc` of root project.
   *
   * @see outputToProject
   * @see outputTo[File]
   * @see outputTo[Appendable]
   */
  abstract fun outputTo(outputStream: OutputStream)

  /**
   * Overwrite the generated code into given [outputFile].
   *
   * Default, the file write out to the `buildSrc` of root project.
   *
   * @see outputToProject
   * @see outputToDirectory
   * @see outputTo[Appendable]
   * @see outputTo[OutputStream]
   */
  abstract fun outputTo(outputFile: File)

  /**
   * Write the generated file to given [outputDir] directory.
   *
   * Default, the file write out to the `buildSrc` of root project.
   *
   * @see outputTo[File]
   */
  abstract fun outputToDirectory(outputDir: File)

  /**
   * Regenerate dependencies whenever gradle syncs.
   *
   * Default, automatically update only when the declaration file and build.gradle file is changed.
   */
  abstract fun alwaysUpdate()

  /**
   * When the predicate returns true, regenerate the dependencies.
   *
   * Default, automatically update only when the declaration file and build.gradle file is changed.
   */
  abstract fun updateWhen(predicate: () -> Boolean)
}

/**
 * Create dependency classes based on the given [configuration]
 */
fun Project.generateDependencies(configuration: DependencyGenerateConfiguration.() -> Unit) {
  DependencyGenerateConfigurationImpl(this).apply(configuration).apply {
    if (this.isChanges()) {
      DependencyGenerator(
        fileName = this.fileName,
        dependencies = this.getDependencies(),
        output = this.getOutputData(),
        autoClose = this.autoClose
      ).generate()
      this.cache()
    }
  }
}
