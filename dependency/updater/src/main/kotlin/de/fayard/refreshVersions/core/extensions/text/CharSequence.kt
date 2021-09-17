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
package de.fayard.refreshVersions.core.extensions.text

internal fun CharSequence.indexOfPrevious(char: Char, startIndex: Int): Int {
  if (startIndex !in 0..lastIndex) throw IndexOutOfBoundsException(startIndex)
  for (i in startIndex downTo 0) {
    val c = this[i]
    if (c == char) return i
  }
  return -1
}

internal interface SkippableIterationScope {
  fun skipIteration(offset: Int)
}

internal inline fun CharSequence.forEachIndexedSkippable(
  action: SkippableIterationScope.(index: Int, c: Char) -> Unit
) {
  var index = 0
  val scope = object : SkippableIterationScope {
    override fun skipIteration(offset: Int) {
      index += offset
    }
  }
  while (index < length) {
    val currentIndex = index++
    val c = this[currentIndex]
    scope.action(currentIndex, c)
  }
}
