package com.meowool.gradle.toolkit.internal

import com.meowool.gradle.toolkit.DependencyFormatter
import kotlinx.coroutines.flow.Flow

/**
 * @author 凛 (https://github.com/RinOrz)
 */
internal interface MapDeclaration {
  val rootClassName: String
  fun toFlow(formatter: DependencyFormatter): Flow<MappedDependency>
}