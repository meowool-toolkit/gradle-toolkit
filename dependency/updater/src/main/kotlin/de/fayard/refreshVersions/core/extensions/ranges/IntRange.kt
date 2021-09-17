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
package de.fayard.refreshVersions.core.extensions.ranges

internal operator fun IntRange.contains(other: IntRange): Boolean {
  return contains(other.first) && contains(other.last)
}

/**
 * EXPECTS A SORTED LIST.
 * Doesn't check for that, so can produce unexpected output if the passed list is not sorted.
 *
 * This function is NOT an operator to avoid ambiguity with minus on [Iterable],
 * since [IntRange] is one.
 */
internal infix fun IntRange.minus(sortedRanges: List<IntRange>): List<IntRange> {
  if (sortedRanges.isEmpty()) return listOf(this)
  return List(sortedRanges.size + 1) { index ->
    when (index) {
      0 -> first until sortedRanges[index].first
      sortedRanges.size -> (sortedRanges.last().last + 1)..last
      else -> (sortedRanges[index - 1].last + 1) until sortedRanges[index].first
    }.let { if (it.isEmpty()) IntRange.EMPTY else it }
  }
}
