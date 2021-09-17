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
@file:Suppress("SpellCheckingInspection", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package com.meowool.gradle.toolkit

import org.gradle.api.Project

/**
 * The optional configuration of Dependencies Mapper.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
interface DependencyMapperExtension {

  /**
   * The prefix of the name of generate the jar with mapped dependencies.
   *
   * The default is 'classes'.
   */
  var jarPrefix: String

  /**
   * Adds or configures the dependency mapper of libraries.
   *
   * @param rootClassName The root class name of the library dependency mapper.
   * @param configuration The configuration of the library dependency mapper.
   */
  fun libraries(
    rootClassName: String = LibraryDependencyDeclaration.DefaultRootClassName,
    configuration: LibraryDependencyDeclaration.() -> Unit = {}
  ): LibraryDependencyDeclaration

  /**
   * Adds or configures the dependency mapper of projects.
   *
   * @param rootClassName The root class name of the project dependency mapper.
   * @param configuration The configuration of the project dependency mapper.
   */
  fun projects(
    rootClassName: String = ProjectDependencyDeclaration.DefaultRootClassName,
    configuration: ProjectDependencyDeclaration.() -> Unit = {}
  ): ProjectDependencyDeclaration

  /**
   * Adds or configures the dependency mapper of plugins.
   *
   * @param rootClassName The root class name of the plugin dependency mapper.
   * @param configuration The configuration of the plugin dependency mapper.
   */
  fun plugins(
    rootClassName: String = PluginDependencyDeclaration.DefaultRootClassName,
    configuration: PluginDependencyDeclaration.() -> Unit = {}
  ): PluginDependencyDeclaration

  /**
   * Adds a [formatter] to formats dependency as mapping path.
   *
   * @see DependencyFormatter.toPath
   */
  fun format(formatter: DependencyFormatter.() -> Unit)

  /**
   * When the predicate returns `true`, remapping the dependencies.
   *
   * By default, only when the dependency declaration changed, will the mapped dependency jar
   * be regenerated. But please note that the code block change like of [format] or [SearchDeclaration.filter] will
   * not be recorded. In this case, clean the build directory of project manually and synchronize gradle.
   *
   * @see alwaysUpdate
   */
  fun updateWhen(predicate: (Project) -> Boolean)

  /**
   * Remappings dependencies whenever gradle sync.
   *
   * By default, only when the dependency declaration changed, will the mapped dependency jar
   * be regenerated. But please note that the code block change like of [format] or [SearchDeclaration.filter] will
   * not be recorded. In this case, clean the build directory of project manually and synchronize gradle.
   *
   * @see updateWhen
   */
  fun alwaysUpdate()
}
