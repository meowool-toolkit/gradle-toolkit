@file:Suppress("unused")

import com.meowool.sweekt.firstCharUppercase

/**
 * Used to format the dependency string.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal class DepFormatter(
  var replaceNotation: (String) -> String = { it },
  var replaceName: (String) -> String = { it },
  var capitalizeFirstLetter: (String) -> Boolean = { true }
) {

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
  fun format(rawNotation: String): String {
    // foo.bar:core-ext -> foo.bar:core.ext
    val normalized = replaceNotation(rawNotation).replace('-', '.').replace('_', '.')

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
    if (capitalizeFirstLetter(this)) this.firstCharUppercase() else this

  /** Each part of the path is a name, and replaced it via [replaceName] */
  private fun String.joinPath(): String =
    splitToSequence('.').joinToString(".") { replaceName(it).mayUpperCase() }

  companion object {
    val Default get() = DepFormatter()
  }
}