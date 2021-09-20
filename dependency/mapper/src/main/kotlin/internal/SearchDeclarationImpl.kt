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

import com.meowool.gradle.toolkit.LibraryDependency
import com.meowool.gradle.toolkit.SearchDeclaration
import com.meowool.gradle.toolkit.internal.client.DependencyRepositoryClient
import com.meowool.sweekt.coroutines.flowOnIO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * @author 凛 (https://github.com/RinOrz)
 */
internal abstract class BaseSearchDeclarationImpl<Result>(values: List<String>) : SearchDeclaration<Result> {
  val data: Data = Data(values)

  override fun fromMavenCentral() {
    data.repositories += DependencyRepository.MavenCentral
  }

  override fun fromGoogle() {
    data.repositories += DependencyRepository.Google
  }

  override fun fromGradlePluginPortal() {
    data.repositories += DependencyRepository.GradlePluginPortal
  }

  override fun fromMvnRepository(fetchExactly: Boolean) {
    data.repositories += DependencyRepository.MvnRepository(fetchExactly)
  }

  override fun filter(predicate: (Result) -> Boolean) {
    data.filters += convertFilter(predicate)
    data.filterCount++
  }

  abstract fun convertFilter(original: (Result) -> Boolean): (LibraryDependency) -> Boolean

  @Serializable
  data class Data(
    val values: List<String> = emptyList(),
    val repositories: MutableSet<DependencyRepository> = mutableSetOf(),
    var filterCount: Int = 0,
  ) {
    @Transient
    val filters: MutableList<(LibraryDependency) -> Boolean> = mutableListOf()

    val clients: List<DependencyRepositoryClient> get() = repositories
      .ifEmpty { setOf(DependencyRepository.MavenCentral) }
      .map { it.client }

    fun merge(other: Data): Data = apply {
      repositories += other.repositories
      filters += other.filters
    }

    suspend fun searchKeywords(): Flow<LibraryDependency> = searchImpl { fetch(it) }
    suspend fun searchPrefixes(): Flow<LibraryDependency> = searchImpl { fetchPrefixes(it) }
    suspend fun searchGroups(): Flow<LibraryDependency> = searchImpl { fetchGroups(it) }

    private suspend inline fun searchImpl(
      crossinline search: DependencyRepositoryClient.(String) -> Flow<LibraryDependency>
    ): Flow<LibraryDependency> = concurrentFlow {
      clients.forEachConcurrently { client ->
        values.forEachConcurrently { value ->
          // Call the real client callback to execute the search
          client.search(value)
            .filter { result -> filters.all { it(result) } }
            .collect(::send)
        }
      }
    }.flowOnIO()

    companion object {
      fun List<Data>.clientUrls() = flatMap { it.clients }.distinct().joinToString { it.baseUrl }
    }
  }
}