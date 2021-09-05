package com.meowool.gradle.toolkit.internal

import com.meowool.sweekt.firstCharUppercase

/**
 * Used to format the dependency string.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal class DepFormatter {
  val notationReplacers = mutableListOf<(String) -> String>()
  val nameReplacers = mutableListOf<(String) -> String>()
  val capitalizeFirstLetters = mutableListOf<(String) -> Boolean>()

  /**
   * Return the notation of dependency after formatting [rawNotation].
   *
   * ```
   * // Inputs:
   * DepFormatter({ it.replace("foo.bar", "foo") })
   *   .format("foo.bar.gav:gav-test")
   *
   * DepFormatter().format("one.dep.user:core-ext")
   *
   * // Outputs:
   * Foo.Bar.Gav.Test
   * One.Dep.User.Core.Ext
   * ```
   */
  fun format(rawNotation: CharSequence): CharSequence {
    // foo.bar:core-ext -> foo.bar:core.ext
    val normalized = notationReplacers.reversed().fold(rawNotation.toString()) { acc, replacer -> replacer(acc) }
      .replace('-', '.')
      .replace('_', '.')

    var group = normalized.substringBefore(':').joinPath()
    val name = normalized.substringAfter(':').joinPath()

    require(name.isNotEmpty())

    // foo.bar:foo.bar -> foo.bar
    // foo.bar.gav:gav -> foo.bar.gav
    // foo.bar.gav:gav-test -> foo.bar.gav.test
    // foo.bar.gav:bar-gav-core -> foo.bar.gav.core
    var shrinking = name
    while (shrinking.isNotEmpty()) {
      // `foo.bar`:foo.bar
      if (shrinking == group) {
        group = ""
        break
      }
      // foo`.bar`:bar
      group = group.removeSuffix(".$shrinking")
      shrinking = shrinking.substringBeforeLast('.', missingDelimiterValue = "")
    }

    return when {
      group.isEmpty() -> name
      else -> "$group.$name"
    }
  }

  private fun String.mayUpperCase(): String =
    if (capitalizeFirstLetters.all { it(this) }) this.firstCharUppercase() else this

  /** Each part of the path is a name, and replaced it via [nameReplacers] */
  private fun String.joinPath(): String {
    val path = this.splitToSequence('.').map {
      val replacedName = nameReplacers.reversed().fold(it) { acc, replacer -> replacer(acc) }
      replacedName.mayUpperCase()
    }
    // Foo.10.20 -> Foo_10_20
    return buildString {
      path.filter { it.isNotEmpty() }.forEachIndexed { index, name ->
        // Previous separator
        when (index) {
          // The beginning of a Java class cannot be a digit
          0 -> if (name.first().isDigit()) append('_')
          else -> append(if (name.first().isDigit()) '_' else '.')
        }
        append(name)
      }
    }
  }

  companion object {
    val Default get() = DepFormatter()
  }
}