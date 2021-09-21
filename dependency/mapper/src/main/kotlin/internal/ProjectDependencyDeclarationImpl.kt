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
package com.meowool.gradle.toolkit.internal

import com.meowool.gradle.toolkit.DependencyFormatter
import com.meowool.gradle.toolkit.ProjectDependencyDeclaration
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.gradle.api.Project

/**
 * @author 凛 (https://github.com/RinOrz)
 */
internal class ProjectDependencyDeclarationImpl(rootClassName: String, val project: Project) :
  ProjectDependencyDeclaration {

  val data: Data by lazy { Data(rootClassName, project.subpaths) }

  override fun mapRootProject(mapped: CharSequence?) {
    data.rootProjectMappedPath = mapped?.toString() ?: project.rootProject.name
  }

  override fun filter(predicate: (Project) -> Boolean) {
    data.filters += predicate
    data.filterCount++
  }

  @Serializable
  data class Data(
    val rootClassName: String,
    var projectPaths: List<String>,
    var rootProjectMappedPath: String? = null,
    val map: MutableSet<String> = mutableSetOf(),
    val mapped: MutableMap<String, String> = mutableMapOf(),
    var filterCount: Int = 0,
  ) : DependencyCollector {
    @Transient
    val filters: MutableList<(Project) -> Boolean> = mutableListOf()

    override suspend fun ConcurrentScope<*>.collect(project: Project, pool: JarPool, formatter: DependencyFormatter) {
      val mappedProjects = mutableListOf<String>()

      suspend fun sendMap(project: Project, mappedPath: CharSequence = project.path) {
        // Do not send if any filter predicate is false
        if (filters.any { it(project).not() }) return

        mappedProjects += project.path

        pool.projectsJar(rootClassName).addDependencyField(
          fullPath = formatter.toPath(mappedPath), // Dir.Sub
          value = project.path, // :dir:sub
        )
      }

      rootProjectMappedPath?.also { sendMap(project.rootProject, mappedPath = it) }
      project.subprojects.forEach { sendMap(it) }

      if (mappedProjects.isNotEmpty()) project.logger.quiet("Project paths: $mappedProjects is collected.")
    }
  }

  companion object {
    private val Project.subpaths: List<String> get() = project.subprojects.map { it.path }
  }
}
