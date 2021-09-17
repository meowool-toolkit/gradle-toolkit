package com.meowool.gradle.toolkit.internal

import com.meowool.sweekt.isEnglishNotPunctuation

/**
 * @author 凛 (https://github.com/RinOrz)
 */
internal object Validator {
  fun validDependency(notation: CharSequence): String {
    var colonExists = false
    return notation.valid("dependency notation") {
      if (it == ':') {
        require(colonExists.not()) {
          "The dependency notation can only has one `:` symbol used to separate `group` and `artifact`, in other words," +
            "the dependency notation cannot contains the version."
        }
        colonExists = true
      }
    }
  }

  fun validPath(path: CharSequence): String = path.valid("path", allowColon = false)

  fun validPluginId(id: CharSequence): String = id.valid("plugin id", allowColon = false)

  private fun CharSequence.valid(type: String, allowColon: Boolean = true, onEach: (Char) -> Unit = {}) = toString().apply {
    forEach {
      require(it.isWhitespace().not()) {
        "The $type `$this` cannot be contains spaces."
      }
      if (allowColon.not()) {
        require(it != ':') { "The $type `$this` cannot be contains ':'" }
        require(it.isDigit() || it.isEnglishNotPunctuation() || it == '.' || it == '-' || it == '_') {
          "The $type `$this` can only contains `.` or `-` or `_` or A-Z or digit."
        }
      } else {
        require(it.isDigit() || it.isEnglishNotPunctuation() || it == '.' || it == ':' || it == '-' || it == '_') {
          "The $type `$this` can only contains `.` or `:` or `-` or `_` or A-Z or digit."
        }
      }
      onEach(it)
    }
  }
}