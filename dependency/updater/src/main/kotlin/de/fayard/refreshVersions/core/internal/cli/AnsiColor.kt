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
package de.fayard.refreshVersions.core.internal.cli

import de.fayard.refreshVersions.core.internal.InternalRefreshVersionsApi

@InternalRefreshVersionsApi
enum class AnsiColor(private val colorNumber: Byte) {
  BLACK(0),
  RED(1),
  GREEN(2),
  YELLOW(3),
  BLUE(4),
  MAGENTA(5),
  CYAN(6),
  WHITE(7);

  companion object {
    private const val prefix = "\u001B"
    const val RESET = "$prefix[0m"
    private val isCompatible = "win" !in System.getProperty("os.name").toLowerCase() // TODO: Support PowerShell?
  }

  val regular get() = if (isCompatible) "$prefix[0;3${colorNumber}m" else ""
  val bold get() = if (isCompatible) "$prefix[1;3${colorNumber}m" else ""
  val underline get() = if (isCompatible) "$prefix[4;3${colorNumber}m" else ""
  val background get() = if (isCompatible) "$prefix[4${colorNumber}m" else ""
  val highIntensity get() = if (isCompatible) "$prefix[0;9${colorNumber}m" else ""
  val boldHighIntensity get() = if (isCompatible) "$prefix[1;9${colorNumber}m" else ""
  val backgroundHighIntensity get() = if (isCompatible) "$prefix[0;10${colorNumber}m" else ""
}
