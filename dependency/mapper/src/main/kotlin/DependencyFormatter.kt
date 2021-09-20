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
@file:Suppress("MemberVisibilityCanBePrivate")

package com.meowool.gradle.toolkit

import com.meowool.gradle.toolkit.internal.removeSuffix
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Processor for formatting raw string.
 *
 * The lambda provides a 'input' string, it may be a dependency, it may be a name, or it may be a path,
 * and then the lambda needs to return a formatted string.
 *
 * Note that if the formatting processor is declared multiple times in [DependencyMapperExtension], the input parameter
 * will be the formatted by the previous declared processor.
 */
typealias FormatProcessor = (input: String) -> String

/**
 * A formatter used to format dependency as path.
 * The final result is [DependencyFormatter.toPath].
 *
 * Formatting order:
 * ```
 * 1. Process all 'onStart {}' in sequence:
 *    onStart(raw): String
 *                    |
 *                    V
 *          onStart(input): String
 *                            |
 *                            V
 *                         .......
 *
 * 2. Process all 'onEachName {}' in sequence:
 *    onEachName(name): String
 *                        |
 *                        .  // Use separators '.' splicing
 *                        |
 *                        V
 *            onEachName(name): String
 *                                |
 *                                V
 *                             .......
 *
 * 3. Process all 'onEnd {}' in sequence:
 *    onEnd(path): String
 *                   |
 *                   V
 *           onEnd(output): String
 *                            |
 *                            V
 *                         .......
 * ```
 *
 * @author 凛 (https://github.com/RinOrz)
 */
@Serializable
data class DependencyFormatter(

  /**
   * Whether to merge duplicate path levels.
   *
   * For example, merge the path 'foo.bar.foo.bar.baz' with duplicate levels as 'foo.bar.baz'.
   * But if they are not connected, just like `foo.bar.break.foo.bar.baz`, it will not merge.
   */
  var isMergeDuplicateLevels: Boolean = true,

  private var startProcessorCount: Int = 0,
  private var nameProcessorCount: Int = 0,
  private var endProcessorCount: Int = 0,
  private var capitalizationPredicateCount: Int = 0,
) {

  /**
   * Merges duplicate path levels.
   *
   * For example, merge the path 'foo.bar.foo.bar.baz' with duplicate levels as 'foo.bar.baz'.
   * But if they are not connected, just like `foo.bar.break.foo.bar.baz`, it will not merge.
   */
  fun mergeDuplicateLevels() {
    isMergeDuplicateLevels = true
  }

  /**
   * Adds a [processor] to processes the input before calling [toPath].
   *
   * @return The result will be used as the input argument of [toPath].
   * @see toPath
   */
  fun onStart(processor: FormatProcessor) {
    startProcessors += processor
    startProcessorCount++
  }

  /**
   * If the [predicate] is `true`, the name of the corresponding dependency will be capitalized.
   *
   * For example `androidx.compose` will capitalize to `Androidx.Compose`
   *
   * [See](https://en.wikipedia.org/wiki/Capitalization)
   */
  fun isCapitalize(predicate: (name: String) -> Boolean) {
    capitalizationPredicates += predicate
    capitalizationPredicateCount++
  }

  /**
   * If the [predicate] is `false`, the name of the corresponding dependency will be capitalized.
   *
   * For example `androidx.compose` will capitalize to `Androidx.Compose`
   *
   * [See](https://en.wikipedia.org/wiki/Capitalization)
   */
  fun notCapitalize(predicate: (name: String) -> Boolean) = isCapitalize { predicate(it).not() }

  /**
   * Adds a [processor] to processes each name part of the dependency.
   *
   * @see transformEachName
   * @see isCapitalize
   */
  fun onEachName(processor: FormatProcessor) {
    nameProcessors += processor
    nameProcessorCount++
  }

  /**
   * Adds a [processor] to processes the [toPath] formatted path.
   *
   * @return The result will be used as the mapped path.
   * @see toPath
   */
  fun onEnd(processor: FormatProcessor) {
    endProcessors += processor
    endProcessorCount++
  }


  ///////////////////////////////////////////////////////////////////////////
  ////                           Internal APIs                           ////
  ///////////////////////////////////////////////////////////////////////////

  @Transient
  internal var startProcessors: MutableList<FormatProcessor> = mutableListOf()
  @Transient
  internal var nameProcessors: MutableList<FormatProcessor> = mutableListOf()
  @Transient
  internal var endProcessors: MutableList<FormatProcessor> = mutableListOf()
  @Transient
  internal var capitalizationPredicates: MutableList<(String) -> Boolean> = mutableListOf()

  /**
   * Formats the dependency as path.
   *
   * For example:
   * ```
   * // Formatting library dependency
   * org.jetbrains.kotlin:kotlin-stdlib -> kotlin.stdlib
   *
   * // Formatting project dependency
   * :core:subpath -> core.subpath
   *
   * // Formatting plugin dependency
   * net.bytebuddy.byte-buddy-gradle-plugin -> bytebuddy.gradle.plugin
   * ```
   *
   * @param input The dependency raw string representation, but if the [DependencyMapperExtension.format] is declared
   *   multiple times, the value may be the formatter processed by the previously declared.
   * @return The path after formatting [input] dependency.
   */
  internal fun toPath(input: CharSequence): String {
    val normalized = startProcessors.fold(input.toString()) { acc, processor -> processor(acc) }
      // foo.bar:core-ext -> foo.bar:core.ext
      .replace('-', '.').replace('_', '.')
      // :parent:sub -> parent.sub
      .replace(':', '.').removePrefix(".")
    val path = normalized.splitToSequence(Dot)
      .mergeDuplicateLevels()
      .map(::transformEachName)
      .joinToPath()
    return endProcessors.fold(path) { acc, processor -> processor(acc) }
  }

  /**
   * Formats each [name] part of the dependency.
   *
   * For example:
   * ```
   * org.jetbrains.kotlin:kotlin-stdlib -> [org, jetbrains, kotlin, kotlin, stdlib]
   * :core:subpath -> [core, subpath]
   * net.bytebuddy.byte-buddy-gradle-plugin -> [net, bytebuddy, byte, buddy, gradle, plugin]
   * ```
   *
   * @return The new name after formatting [name].
   */
  private fun transformEachName(name: String): String {
    val isCapitalize = capitalizationPredicates.all { it(name) }
    val processedName = nameProcessors.fold(name) { acc, processor -> processor(acc) }
    return when {
      isCapitalize -> processedName.capitalize()
      else -> processedName
    }
  }

  /**
   * Merges the path with duplicate levels.
   *
   * @see isMergeDuplicateLevels
   * */
  private fun Sequence<String>.mergeDuplicateLevels(): Sequence<String> {
    if (isMergeDuplicateLevels.not()) return this

    // The pool storing each name, each looping is taken from here, until the pool is empty.
    //   For example `A.B.C.B.C.D`
    //     Left part:  [A]
    val left = mutableListOf<String>()
    //     Right part: [B, C, B, C, D]
    val right = this.toMutableList()

    while (right.isNotEmpty()) {
      // Take the first from the pool and splice to the end of 'before'
      //   Left part:  [A, B]
      //   Right part: [C, B, C, D]
      left.add(right.removeFirst())

      // Loop to drop the last one of the new right part:
      //   -> [B, C, D]
      //   -> [B, C]
      //   -> [B]
      val shrinking = right.toMutableList()
      while (shrinking.isNotEmpty()) {
        if (shrinking == left) {
          left.clear()
          break
        }
        // before: [A, B, C], shrinking: [B, C]
        //   -> [A]
        left.removeSuffix(shrinking)
        // Drop the last
        shrinking.removeLast()
      }
    }

    return sequence {
      yieldAll(left)
      yieldAll(right)
    }
  }

  private fun Sequence<String>.joinToPath(): String {
    val builder = StringBuilder()
    // Fix https://www.scala-sbt.org/1.x/docs/Library-Dependencies.html#Getting+the+right+Scala+version+with
    // Foo.10.20 -> Foo_10_20
    this.filter { it.isNotEmpty() }.forEachIndexed { index, name ->
      // Append separator
      when {
        name.first().isDigit() -> builder.append('_')
        index != 0 -> builder.append(Dot)
      }
      // Append name
      builder.append(name)
    }
    return builder.toString()
  }

  companion object {
    internal const val Dot = '.'
  }
}
