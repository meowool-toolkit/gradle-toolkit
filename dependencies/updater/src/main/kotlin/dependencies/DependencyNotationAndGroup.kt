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
package dependencies

internal abstract class DependencyNotationAndGroup(
  group: String,
  name: String
) : CharSequence {

  protected val artifactPrefix = "$group:$name"

  internal val backingString = "$artifactPrefix:_"
  override val length get() = backingString.length
  override fun get(index: Int) = backingString[index]

  override fun subSequence(
    startIndex: Int,
    endIndex: Int
  ) = backingString.subSequence(startIndex = startIndex, endIndex = endIndex)

  override fun toString(): String = backingString
}
