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

  override fun toFlow(formatter: DependencyFormatter): Flow<MappedDependency> = channelFlow {
    rootProjectMapping?.also {
      send(MappedDependency(
        dependency = project!!.rootProject.path,
        mappedPath = formatter.toPath(it),
      ))
    }
    project!!.subprojects.map { it.path }.forEachConcurrently {
      send(MappedDependency(
        dependency = it,
        mappedPath = formatter.toPath(it),
      ))
    }
  }
}