@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.meowool.gradle.toolkit.internal.client

import com.meowool.gradle.toolkit.LibraryDependency
import com.meowool.gradle.toolkit.internal.DependencyRepository
import com.meowool.gradle.toolkit.internal.forEachConcurrently
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.logging.HttpLoggingInterceptor

/**
 * A http client of [DependencyRepository.MavenCentral].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal class MavenCentralClient(
  logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE
) : DependencyRepositoryClient(baseUrl = "https://search.maven.org/solrsearch", logLevel) {

  override fun fetch(keyword: String): Flow<LibraryDependency> = cache(Fetch.Keyword(keyword)) {
    fetchImpl(keyword)
  }

  override fun fetchGroups(group: String): Flow<LibraryDependency> = cache(Fetch.Group(group)) {
    fetchImpl("g:\"$group\"")
  }

  private fun fetchImpl(keyword: String): Flow<LibraryDependency> = pagesFlow { page ->
    val offset = (page - 1) * 1000
    // Only get up to 1000 results at a time to avoid excessive server pressure
    get<Search>("select?q=$keyword&start=$offset&rows=1000").result.dependencies
      .takeIf { it.isNotEmpty() }
      ?.forEachConcurrently { send(LibraryDependency(it.toString())) }
  }

  @Serializable private data class Search(@SerialName("response") val result: Result) {
    @Serializable data class Result(@SerialName("docs") val dependencies: List<Notation>) {
      @Serializable data class Notation(@SerialName("g") val group: String, @SerialName("a") val artifact: String) {
        override fun toString(): String = "$group:$artifact"
      }
    }
  }
}
