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
@file:UseContextualSerialization(Duration::class)

package com.meowool.gradle.toolkit.internal

import com.meowool.gradle.toolkit.LibraryDependency
import com.meowool.gradle.toolkit.SearchDeclaration
import com.meowool.gradle.toolkit.internal.client.DependencyRepositoryClient
import com.meowool.sweekt.coroutines.flowOnIO
import com.meowool.sweekt.datetime.minutes
import com.meowool.sweekt.throwIf
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retry
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseContextualSerialization
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

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
    data.repositories += when {
      fetchExactly -> DependencyRepository.MvnRepository
      else -> DependencyRepository.MvnExactlyRepository
    }
  }

  override fun requireResultAtLeast(minCount: Int, retryIfMissing: Boolean, retryTimeout: Duration) {
    data.minResultsRequired = minCount
    data.retryIfMissing = retryIfMissing
    data.retryTimeout = retryTimeout
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
    var minResultsRequired: Int = 0,
    var retryIfMissing: Boolean = true,
    var retryTimeout: Duration = Duration.ofMinutes(1),
  ) {
    @Transient
    val filters: MutableList<(LibraryDependency) -> Boolean> = mutableListOf()

    val clients: List<DependencyRepositoryClient>
      get() = repositories
        .ifEmpty { setOf(DependencyRepository.MavenCentral) }
        .map { it.client }

    fun merge(other: Data): Data = apply {
      repositories += other.repositories
      filters += other.filters
    }

    suspend fun searchKeywords(isConcurrently: Boolean): Flow<LibraryDependency> =
      searchImpl(isConcurrently) { fetch(it) }
    suspend fun searchPrefixes(isConcurrently: Boolean): Flow<LibraryDependency> =
      searchImpl(isConcurrently) { fetchPrefixes(it) }
    suspend fun searchGroups(isConcurrently: Boolean): Flow<LibraryDependency> =
      searchImpl(isConcurrently) { fetchGroups(it) }

    private suspend fun searchImpl(
      isConcurrently: Boolean,
      search: DependencyRepositoryClient.(String) -> Flow<LibraryDependency>,
    ): Flow<LibraryDependency> {
      var lastRetry: Duration? = null
      var retryIfMissing = retryIfMissing

      return concurrentFlow(isConcurrently) {
        val count = AtomicInteger()

        try {
          // Remaining timeout
          withTimeout(lastRetry?.let { retryTimeout - it }) {
            clients.forEach { client ->
              values.forEachConcurrently { value ->
                // Call the real client callback to execute the search
                client.search(value)
                  .distinct()
                  .onEach { count.incrementAndGet() }
                  .filter { lib -> filters.all { it(lib) } }
                  .collect(::send)
              }
            }
            throwIf(count.get() < minResultsRequired) {
              // Start waiting for retry timeout
              if (lastRetry == null) lastRetry = Duration.ofMillis(System.currentTimeMillis())
              ResultsMissingException(count.get())
            }
          }
        } catch (e: TimeoutCancellationException) {
          retryIfMissing = false
          throw ResultsMissingException(count.get())
        }
      }.flowOnIO().retry(Long.MAX_VALUE) { retryIfMissing && it is ResultsMissingException }
    }

    private inner class ResultsMissingException(count: Int) :
      IllegalStateException("Need at least $minResultsRequired results, but only $count results found, search timeout $retryTimeout.")

    companion object {
      fun List<Data>.clientUrls() = flatMap { it.clients }.distinct().joinToString { it.baseUrl }
    }
  }
}
