package com.meowool.gradle.toolkit

import org.gradle.api.Project

/**
 * Used to declare how to map project dependencies.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
interface ProjectDependencyDeclaration {

  /**
   * Map the root project as the specified [mapped] path/name.
   *
   * By default, the generated mapping class does not contain the root project, please manually call this function to
   * map the root project.
   *
   * @param mapped The mapped name or directory, if it is `null`, use [Project.getName].
   */
  fun mapRootProject(mapped: CharSequence? = null)

  /**
   * If the [predicate] is `true`, the corresponding [Project] will be mapped, otherwise it will not be mapped.
   */
  fun filter(predicate: Project.() -> Boolean)

  /**
   * If the [predicate] is `false`, the corresponding [Project] will be mapped, otherwise it will not be mapped.
   */
  fun filterNot(predicate: Project.() -> Boolean) = filter { predicate().not() }

  companion object {
    const val DefaultRootClassName = "Projects"
  }
}