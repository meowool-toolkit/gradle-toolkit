@file:Suppress("EXPERIMENTAL_API_USAGE")

import io.ktor.client.features.logging.*
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.retry
import org.jsoup.nodes.Document

/**
 * A http client of [RemoteRepositories.google].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal class GoogleMavenClient(
  logLevel: LogLevel = LogLevel.NONE
) : DependencyRepositoryClient(baseUrl = "https://maven.google.com", logLevel) {

  override fun fetch(keyword: String): Flow<Dependency> = cache(Fetch.Keyword(keyword)) {
    fetchAllDependencies().filter { it.contains(keyword) }
  }

  override fun fetchGroups(group: String): Flow<Dependency> = cache(Fetch.Group(group)) {
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
            Dependency("$group:$it")
          }
        }
        // Filter out artifacts through all cached dependencies
        else -> sendList {
          allDependencies.filter { it.group == group }
        }
      }
    }.retry()
  }

  internal fun fetchAllDependencies(): Flow<Dependency> = cache(Fetch.Any) {
    fetchAllGroups().flatMapConcurrently { fetchGroups(it) }
  }

  private fun fetchAllGroups() = channelFlow<String> {
    // <metadata>
    //   <android.arch.core/>
    // </metadata>
    sendChildren(getOrNull("master-index.xml"))
  }.retry()

  private suspend inline fun <T> ProducerScope<T>.sendChildren(
    document: Document?,
    crossinline map: (String) -> T = @Suppress("UNCHECKED_CAST") { it as T }
  ) = document?.children()?.firstOrNull()?.children()?.forEachConcurrently { send(map(it.tagName())) }
}