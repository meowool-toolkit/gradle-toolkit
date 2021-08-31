@file:Suppress("PLUGIN_IS_NOT_ENABLED", "EXPERIMENTAL_API_USAGE")

import io.ktor.client.features.logging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A http client of [RemoteRepositories.mavenCentral].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal class MavenCentralClient(
  logLevel: LogLevel = LogLevel.NONE,
) : DependencyRepositoryClient(baseUrl = "https://search.maven.org/solrsearch", logLevel) {

  override fun fetch(keyword: String): Flow<Dependency> = cache(Fetch.Keyword(keyword)) { fetchImpl(keyword) }

  override fun fetchGroups(group: String): Flow<Dependency> = cache(Fetch.Group(group)) { fetchImpl("g:\"$group\"") }

  private fun fetchImpl(keyword: String): Flow<Dependency> = pagesFlow { page ->
    val offset = (page - 1) * 1000
    // Only get up to 1000 results at a time to avoid excessive server pressure
    get<Search>("select?q=$keyword&start=$offset&rows=1000").result.dependencies
      .takeIf { it.isNotEmpty() }
      ?.forEachConcurrently { send(Dependency(it.toString())) }
  }.retry(10)

  @Serializable private data class Search(@SerialName("response") val result: Result) {
    @Serializable data class Result(@SerialName("docs") val dependencies: List<Notation>) {
      @Serializable data class Notation(@SerialName("g") val group: String, @SerialName("a") val artifact: String) {
        override fun toString(): String = "$group:$artifact"
      }
    }
  }
}
