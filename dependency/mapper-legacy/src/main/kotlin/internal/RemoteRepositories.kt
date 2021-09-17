package com.meowool.gradle.toolkit.internal

import com.meowool.gradle.toolkit.internal.client.*

/**
 * Defined remote dependency repositories.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
@InternalGradleToolkitApi
class RemoteRepositories internal constructor(private val remote: RemoteDependencies) {

  /**
   * Adds [Maven Central Repository](https://search.maven.org/) as a remote dependency repository.
   */
  fun mavenCentral() {
    clientIds += 0
    if (remote.clients.none { it is MavenCentralClient }) remote.clients += MavenCentralClient()
  }

  /**
   * Adds [Google's Maven Repository](https://maven.google.com/) as a remote dependency repository.
   */
  fun google() {
    clientIds += 1
    if (remote.clients.none { it is GoogleMavenClient }) remote.clients += GoogleMavenClient()
  }

  /**
   * Adds [MvnRepository](https://mvnrepository.com/) as a remote dependency repository.
   *
   * @param fetchExactly Some scala dependencies have special version. By default, mvn repository will not resolve them.
   *   Only when the value is `true` will it spend more time to resolve them. For example,
   *   `org.scala-lang:scala3-library`, which is actually `org.scala-lang:scala3-library_3`. For more details, see
   *   [Scala docs](https://www.scala-sbt.org/1.x/docs/Library-Dependencies.html#Getting+the+right+Scala+version+with)
   */
  fun mvnrepository(fetchExactly: Boolean = false) {
    clientIds += if (fetchExactly) 3 else 2
    if (remote.clients.none { it is MvnRepositoryClient && it.fetchExactly == fetchExactly })
      remote.clients += MvnRepositoryClient(fetchExactly)
  }


  ///////////////////////////////////////////////////////////////////////////
  ////                           Internal APIs                           ////
  ///////////////////////////////////////////////////////////////////////////

  internal val clientIds = mutableSetOf(0)
  companion object {
    internal fun RemoteDependencies.resolveClients(clientIds: Set<Int>) = clientIds.map { id ->
      when(id) {
        0 -> clients.first { it is MavenCentralClient }
        1 -> clients.first { it is GoogleMavenClient }
        2 -> clients.first { it is MvnRepositoryClient && it.fetchExactly.not() }
        3 -> clients.first { it is MvnRepositoryClient && it.fetchExactly }
        else -> error("id = $id")
      }
    }
  }
}