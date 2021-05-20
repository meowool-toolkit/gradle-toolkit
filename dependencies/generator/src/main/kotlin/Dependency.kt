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
@file:Suppress("SpellCheckingInspection")

/**
 * @author 凛 (https://github.com/RinOrz)
 */
internal data class Dependency(
  private var oldParent: String,
  private var oldName: String,
  val full: String,
) {
  private val excludeSymbols = arrayOf('-', '_')

  val path by lazy {
    buildString {
      append(oldParent)
      if (oldName.isNotEmpty()) {
        append('.')
        append(oldName)
      }
    }.removeSuffix(".").removePrefix(".")
  }

  val parent by lazy { if (path.contains('.')) path.substringBeforeLast('.') else "" }
  val name by lazy { path.substringAfterLast('.') }

  val pathSplit by lazy { path.split('.') }

  fun takePath(n: Int) = pathSplit.take(n).joinToString(".")

  fun standardization(
    simplifyClassName: Boolean,
    upperCamelCaseWhen: (String) -> Boolean,
    classNameTransformer: (String) -> String,
    classParentTransformer: (String) -> String
  ) {
    fun String.upperCamelCase() = capitalize(upperCamelCaseWhen(this))

    fun String.standardCase() =
      // 转换所有非法符号为 .
      excludeSymbols.fold(this) { acc, symbol -> acc.replace(symbol, '.') }
        .split('.')
        .joinToString(".") { classNameTransformer(it).upperCamelCase() }

    oldName = oldName.standardCase()
    oldParent = classParentTransformer(oldParent).standardCase()

    if (simplifyClassName) {
      // 工件名开头如果和组结尾相同则简化
      val nameList = oldName.split('.').toMutableList()
      if (oldParent.endsWith(nameList[0])) nameList.removeAt(0)
      oldName = nameList.joinToString(".")
    }
  }
}
