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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.gradle.api.Project

/**
 * @author 凛 (https://github.com/RinOrz)
 */
@Serializable
internal class ProjectDependencyDeclarationImpl(
  override val rootClassName: String,
  @Transient
  private val project: Project? = null,
) : ProjectDependencyDeclaration, MapDeclaration {
  private var rootProjectMapping: String? = null

  @Transient
  private var filters = mutableListOf<(Project) -> Boolean>()
  private var filterCount = 0

  override fun mapRootProject(mapped: CharSequence?) {
    rootProjectMapping = mapped?.toString() ?: project!!.rootProject.name
  }

  override fun filter(predicate: Project.() -> Boolean) {
    filters += predicate
    filterCount++
  }

  override fun toFlow(
    parent: DependencyMapperExtensionImpl,
    formatter: DependencyFormatter
  ): Flow<MappedDependency> = channelFlow {
    rootProjectMapping?.also {
      send(
        MappedDependency(
          dependency = project!!.rootProject.path,
          mappedPath = formatter.toPath(it),
        )
      )
    }
    project!!.subprojects.map { it.path }.forEachConcurrently {
      send(
        MappedDependency(
          dependency = it,
          mappedPath = formatter.toPath(it),
        )
      )
    }
  }
}
