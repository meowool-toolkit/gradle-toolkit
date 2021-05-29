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
 * Represents a dependency data.
 *
 * @param originParent the unprocessed string of origin parent
 * @param originName the unprocessed string of origin name
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal data class Dependency(
  private var originParent: String,
  private var originName: String,
  val full: String,
) {
  private val excludeSymbols = arrayOf('-', '_')

  val path by lazy {
    buildString {
      append(originParent)
      if (originName.isNotEmpty()) {
        append('.')
        append(originName)
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
    classParentTransformer: (String) -> String,
  ) {
    fun String.firstCharUppercase() = firstCharUppercase(upperCamelCaseWhen(this))

    fun String.standardCase() =
      // 转换所有非法符号为 .
      excludeSymbols.fold(this) { acc, symbol -> acc.replace(symbol, '.') }
        .split('.')
        .joinToString(".") { classNameTransformer(it).firstCharUppercase() }

    originName = originName.standardCase()
    originParent = classParentTransformer(originParent).standardCase()

    if (simplifyClassName) {
      // 工件名开头如果和组结尾相同则简化
      // com.a.b:b -> com.a.b
      val nameList = originName.split('.').toMutableList()
      if (originParent.endsWith(nameList[0])) nameList.removeAt(0)
      originName = nameList.joinToString(".")
    }
  }
}
