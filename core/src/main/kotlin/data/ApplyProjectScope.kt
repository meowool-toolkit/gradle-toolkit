/*
 * Copyright (c) $\YEAR. The Meowool Organization Open Source Project
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
 * Apply scope, project that meet the scope will be applied.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
enum class ApplyProjectScope {
  /** Only applies to the current project. */
  CurrentProject,

  /** Applies to all projects but exclude the current project. */
  SubProjects,

  /** Applies to all projects, contains [CurrentProject] and [SubProjects]. */
  AllProjects,
}
