@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.meowool.gradle.toolkit.internal.client

import com.meowool.gradle.toolkit.LibraryDependency
import com.meowool.gradle.toolkit.PluginId
import com.meowool.gradle.toolkit.internal.DependencyRepository
import com.meowool.gradle.toolkit.internal.forEachConcurrently
import com.meowool.gradle.toolkit.internal.retryConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.nodes.Document

/**
 * A http client of [DependencyRepository.GradlePluginPortal].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal class GradlePluginClient(
  logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE
) : DependencyRepositoryClient(baseUrl = "https://plugins.gradle.org", logLevel) {

  override fun fetch(keyword: String): Flow<LibraryDependency> = cache(Fetch.Keyword(keyword)) {
    pagesFlow(initial = 0) { page ->
      get<Document>("search?term=$keyword&page=$page")
        .select(".plugin-id")
        .takeIf { it.isNotEmpty() }
        ?.forEachConcurrently { send(PluginId(it.text()).toLibraryDependency()) }
    }
  }

  override fun fetchGroups(group: String): Flow<LibraryDependency> = cache(Fetch.Group(group)) {
    fetchStartsWith(group).filter { it.group == group }
  }

  override fun fetchStartsWith(startsWith: String): Flow<LibraryDependency> = cache(Fetch.StartsWith(startsWith)) {
    channelFlow {
      suspend fun impl(url: String) {
        val list = getOrNull<Document>(url)
          ?.select("pre > a")
          ?.map { it.text() }
          ?.takeIf { it.isNotEmpty() } ?: return

        if (list.any { it == "maven-metadata.xml" }) launch {
          // Is a plugin path
          val metadata = get<Document>("$url/maven-metadata.xml")
          val versions = metadata.select("versions > version").map { it.text() }
          launch {
            send(LibraryDependency(
              group = metadata.selectFirst("groupId")!!.text(),
              artifact = metadata.selectFirst("artifactId")!!.text()
            ))
          }
          // Exclude version directories and then recursive other subpaths
          list.filter { versions.contains(it).not() && it.endsWith('/') }.forEachConcurrently { impl("$url/$it") }
        } else {
          // Recursive subpaths
          list.filter { it.endsWith('/') }.forEachConcurrently { impl("$url/$it") }
        }
      }

      impl("m2/" + startsWith.replace('.', '/'))
    }.retryConnection()
  }
}