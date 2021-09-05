package com.meowool.gradle.toolkit.internal

/**
 * Represents a dependency meta-information.
 *
 * For example group is `foo.bar`, artifact is `gav`:
 * ```
 * Dependency("foo.bar:gav")
 * ```
 *
 * @author 凛 (https://github.com/RinOrz)
 */
@InternalGradleToolkitApi
class Dependency internal constructor(private val notation: String): CharSequence by notation {
  val group: String = notation.substringBefore(':')
  val artifact: String = notation.substringAfter(':')
  override fun hashCode(): Int = toString().hashCode()
  override fun equals(other: Any?): Boolean = toString() == other?.toString()
  override fun toString(): String = notation
}