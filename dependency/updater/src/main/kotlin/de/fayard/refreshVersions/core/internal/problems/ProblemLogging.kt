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
 * 除如果您正在修改此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
package de.fayard.refreshVersions.core.internal.problems

import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger

internal fun Logger.log(problem: Problem<Any>) {
  val logLevel = when (problem.level) {
    Problem.Level.Warning -> LogLevel.WARN
    Problem.Level.Error -> LogLevel.ERROR
    Problem.Level.Error.Fatal -> LogLevel.ERROR
  }
  val levelLetter = when (problem.level) {
    Problem.Level.Warning -> 'w'
    is Problem.Level.Error -> 'e'
  }
  val affectedFile = problem.affectedFile
  val message = problem.errorMessage
  when (affectedFile) {
    null -> log(logLevel, message)
    else -> {
      // This log level format is recognized by IntelliJ IDEA.
      log(logLevel, "$levelLetter: ${affectedFile.path}:\n$message")
    }
  }
}
