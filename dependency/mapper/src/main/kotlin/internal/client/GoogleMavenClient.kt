@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.meowool.gradle.toolkit.internal.client

import com.meowool.gradle.toolkit.LibraryDependency
import com.meowool.gradle.toolkit.internal.DependencyRepository
import com.meowool.gradle.toolkit.internal.flatMapConcurrently
import com.meowool.gradle.toolkit.internal.forEachConcurrently
import com.meowool.gradle.toolkit.internal.retryConnection
import com.meowool.gradle.toolkit.internal.sendList
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.retry
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

/**
 * A http client of [DependencyRepository.Google].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal class GoogleMavenClient(
  logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE
) : DependencyRepositoryClient(baseUrl = "https://maven.google.com", logLevel) {

  override fun fetch(keyword: String): Flow<LibraryDependency> = cache(Fetch.Keyword(keyword)) {
    fetchAllDependencies().filter { it.contains(keyword) }
  }

  override fun fetchGroups(group: String): Flow<LibraryDependency> = cache(Fetch.Group(group)) {
    channelFlow {
      when (val allDependencies = cache.getIfPresent(Fetch.Any)) {
        // The cache of all dependencies is expired
        null -> {
          val groupPath = group.replace('.', '/')
          // <android.arch.core>
          //   <core/>
          //   <runtime/>
          // </android.arch.core>
          sendChildren(getOrNull("$groupPath/group-index.xml")) {
            LibraryDependency("$group:$it")
          }
        }
        // Filter out artifacts through all cached dependencies
        else -> sendList {
          allDependencies.filter { it.group == group }
        }
      }
    }.retryConnection()
  }

  fun fetchAllDependencies(): Flow<LibraryDependency> = cache(Fetch.Any) {
    fetchAllGroups().flatMapConcurrently { fetchGroups(it) }
  }

  private fun fetchAllGroups() = channelFlow<String> {
    // <metadata>
    //   <android.arch.core/>
    // </metadata>
    sendChildren(getOrNull("master-index.xml"))
  }.retryConnection()

  private suspend inline fun <T> ProducerScope<T>.sendChildren(
    document: Document?,
    crossinline map: (String) -> T = @Suppress("UNCHECKED_CAST") { it as T }
  ) = document?.children()?.firstOrNull()?.children()?.forEachConcurrently { send(map(it.tagName())) }
}