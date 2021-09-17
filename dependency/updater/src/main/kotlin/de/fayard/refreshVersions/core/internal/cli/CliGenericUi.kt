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
import java.util.Scanner

@InternalRefreshVersionsApi
interface CliGenericUi {

  companion object {
    operator fun invoke(): CliGenericUi =
      CliGenericUiImpl()
  }

  fun askBinaryQuestion(
    question: String,
    trueChoice: String,
    falseChoice: String
  ): Boolean

  fun showMenuAndGetIndexOfChoice(
    header: String,
    footer: String,
    numberedEntries: List<String>
  ): MenuEntryIndex
}

@InternalRefreshVersionsApi
inline class MenuEntryIndex(val value: Int)

private class CliGenericUiImpl : CliGenericUi {

  override fun askBinaryQuestion(
    question: String,
    trueChoice: String,
    falseChoice: String
  ): Boolean {
    while (true) {
      print(AnsiColor.WHITE.boldHighIntensity)
      print(AnsiColor.BLUE.background)
      print(question)
      println(AnsiColor.RESET)
      print(AnsiColor.WHITE.boldHighIntensity)
      print(AnsiColor.GREEN.background)
      print("$trueChoice/$falseChoice")
      println(AnsiColor.RESET)
      when (scanner.nextLine()) {
        trueChoice -> return true
        falseChoice -> return false
        else -> {
          print(AnsiColor.RED.bold)
          print("Unexpected input, please, try again")
          println(AnsiColor.RESET)
        }
      }
    }
  }

  override fun showMenuAndGetIndexOfChoice(
    header: String,
    footer: String,
    numberedEntries: List<String>
  ): MenuEntryIndex {
    println(header)
    println()
    numberedEntries.forEachIndexed { index, entryText ->
      val number = index + 1
      print("$number. ")
      println(entryText)
    }
    println()
    print(AnsiColor.WHITE.boldHighIntensity)
    print(AnsiColor.BLUE.background)
    print(footer)
    println(AnsiColor.RESET)
    return MenuEntryIndex(scanner.nextInt() - 1)
  }

  private val scanner = Scanner(System.`in`)
}
