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
package com.meowool.gradle.toolkit.internal

import com.meowool.sweekt.isEnglishNotPunctuation

/**
 * @author 凛 (RinOrz)
 */
internal object Validator {
  fun validDependency(notation: CharSequence): String {
    var colonExists = false
    return notation.valid("dependency notation") {
      if (it == ':') {
        require(colonExists.not()) {
          "The dependency notation `$notation` can only has one `:` symbol used to separate `group` and `artifact`, in other words," +
            "it cannot contains the version."
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
