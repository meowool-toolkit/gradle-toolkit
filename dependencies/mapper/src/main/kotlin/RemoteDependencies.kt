@file:Suppress("EXPERIMENTAL_API_USAGE", "MemberVisibilityCanBePrivate")

import RemoteRepositories.Companion.resolveClients
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onCompletion

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class RemoteDependencies {

  /**
   * Sets remote repositories, all remote dependencies defined will be fetched from the configured
   * repositories by default.
   *
   * By default, all dependencies fetch from [RemoteRepositories.mavenCentral].
   *
   * For example, add repositories to fetch from `https://maven.google.com/` and `https://mvnrepository.com/`:
   * ```
   * repositories {
   *   google()
   *   mvnrepository()
   * }
   * ```
   */
  fun repositories(configuration: RemoteRepositories.() -> Unit) {
    defaultClientIds += RemoteRepositories(this).apply { configuration() }.clientIds
  }

  /**
   * Adds the specified keywords to map related remote dependencies.
   *
   * @param dependencyKeywords Keywords of the remote dependencies that needs to be mapped.
   */
  fun keywords(
    vararg dependencyKeywords: String,
    repositories: (RemoteRepositories.() -> Unit)? = null,
    filter: (Dependency) -> Boolean = { true }
  ) = keywords.add(DependenciesFetchParameter(
    dependencyKeywords,
    repositories?.let { RemoteRepositories(this).apply(it).clientIds },
    filter
  ))

  /**
   * Adds the specified groups to map related remote dependencies.
   *
   * @param dependencyGroupIds Group ids of the remote dependencies that needs to be mapped.
   */
  fun groups(
    vararg dependencyGroupIds: String,
    repositories: (RemoteRepositories.() -> Unit)? = null,
    filter: (Dependency) -> Boolean = { true }
  ) = groups.add(DependenciesFetchParameter(
    dependencyGroupIds,
    repositories?.let { RemoteRepositories(this).apply(it).clientIds },
    filter
  ))

  /**
   * Adds the specified keyword to map remote dependencies related to the beginning of the keywords.
   *
   * @param beginningKeywords Group ids of the remote dependencies that needs to be mapped.
   */
  fun startsWith(
    vararg beginningKeywords: String,
    repositories: (RemoteRepositories.() -> Unit)? = null,
    filter: (Dependency) -> Boolean = { true }
  ) = starts.add(DependenciesFetchParameter(
    beginningKeywords,
    repositories?.let { RemoteRepositories(this).apply(it).clientIds },
    filter
  ))

  /**
   * Set a [filter] to filter the remote dependencies after fetched.
   *
   * @param predicate If the predicate is `false`, dependency will be excluded.
   */
  fun filter(predicate: (Dependency) -> Boolean) {
    filters += predicate
  }


  ///////////////////////////////////////////////////////////////////////////
  ////                           Internal APIs                           ////
  ///////////////////////////////////////////////////////////////////////////

  private val filters = mutableListOf<(Dependency) -> Boolean>()
  internal val defaultClientIds = mutableSetOf(0)
  internal val keywords = mutableListOf<DependenciesFetchParameter>()
  internal val groups = mutableListOf<DependenciesFetchParameter>()
  internal val starts = mutableListOf<DependenciesFetchParameter>()
  internal val clients = mutableSetOf<DependencyRepositoryClient>(MavenCentralClient())

  internal fun fetch() = channelFlow {
    // Fetch all dependencies
    requestAll(keywords) { fetch(it) }
    requestAll(groups) { fetchGroups(it) }
    requestAll(starts) { fetchStartsWith(it) }
  }.filter { dep ->
    filters.all { it(dep) }
  }.onCompletion {
    clients.forEach { it.close() }
  }

  private suspend fun ProducerScope<Dependency>.requestAll(
    parameters: List<DependenciesFetchParameter>,
    fetcher: DependencyRepositoryClient.(value: String) -> Flow<Dependency>
  ) = parameters.forEachConcurrently { param ->
    // Send request with all clients
    resolveClients(param.clientIds ?: defaultClientIds).forEachConcurrently { client ->
      param.requests.forEachConcurrently { value ->
        sendAll { client.fetcher(value).filter { param.resultFilter(it) } }
      }
    }
  }
}