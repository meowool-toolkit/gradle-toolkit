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
package com.meowool.gradle.toolkit.internal

import com.meowool.gradle.toolkit.SearchDeclaration
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * @author 凛 (https://github.com/RinOrz)
 */
@Serializable
internal class SearchDeclarationImpl<Result>(val values: List<String> = emptyList()) : SearchDeclaration<Result> {
  private val repositories = mutableSetOf<DependencyRepository>()

  @Transient
  var filters = mutableListOf<(Result) -> Boolean>()
  private var filterCount = 0

  fun getClients() = repositories.ifEmpty { setOf(DependencyRepository.MavenCentral) }.map { it.client }

  fun copyFrom(source: SearchDeclarationImpl<Result>) = apply {
    repositories += source.repositories
    filters += source.filters
  }

  override fun fromMavenCentral() {
    repositories += DependencyRepository.MavenCentral
  }

  override fun fromGoogle() {
    repositories += DependencyRepository.Google
  }

  override fun fromGradlePluginPortal() {
    repositories += DependencyRepository.GradlePluginPortal
  }

  override fun fromMvnRepository(fetchExactly: Boolean) {
    repositories += DependencyRepository.MvnRepository(fetchExactly)
  }

  override fun filter(predicate: (Result) -> Boolean) {
    filters += predicate
    filterCount++
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is SearchDeclarationImpl<*>) return false

    if (values != other.values) return false
    if (repositories != other.repositories) return false
    if (filterCount != other.filterCount) return false

    return true
  }

  override fun hashCode(): Int {
    var result = values.hashCode()
    result = 31 * result + repositories.hashCode()
    result = 31 * result + filterCount
    return result
  }
}
