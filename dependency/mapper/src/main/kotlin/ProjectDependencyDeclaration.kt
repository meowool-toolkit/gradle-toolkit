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
package com.meowool.gradle.toolkit

import org.gradle.api.Project

/**
 * Used to declare how to map project dependencies.
 *
 * @author 凛 (RinOrz)
 */
interface ProjectDependencyDeclaration {

  /**
   * Map the root project as the specified [mapped] path/name.
   *
   * By default, the generated mapping class does not contain the root project, please manually call this function to
   * map the root project.
   *
   * @param mapped The mapped name or path, if it is `null`, use [Project.getName].
   */
  fun mapRootProject(mapped: CharSequence? = null)

  /**
   * If the [predicate] is `true`, the corresponding [Project] will be mapped, otherwise it will not be mapped.
   */
  fun filter(predicate: (Project) -> Boolean)

  /**
   * If the [predicate] is `false`, the corresponding [Project] will be mapped, otherwise it will not be mapped.
   */
  fun filterNot(predicate: (Project) -> Boolean) = filter { predicate(it).not() }

  companion object {
    const val DefaultRootClassName = "Projects"
  }
}
