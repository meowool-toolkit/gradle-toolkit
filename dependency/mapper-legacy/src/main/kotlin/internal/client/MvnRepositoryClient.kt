@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.meowool.gradle.toolkit.internal.client

import com.meowool.gradle.toolkit.internal.Dependency
import com.meowool.gradle.toolkit.internal.*
import com.meowool.sweekt.substringBefore
import io.ktor.client.features.logging.*
import kotlinx.coroutines.flow.Flow
import org.jsoup.nodes.Document

/**
 * A http client of [RemoteRepositories.mvnrepository].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal class MvnRepositoryClient(
  internal val fetchExactly: Boolean,
  logLevel: LogLevel = LogLevel.NONE,
) : DependencyRepositoryClient(baseUrl = "https://mvnrepository.com", logLevel) {

  override fun fetch(keyword: String): Flow<Dependency> = cache(Fetch.Keyword(keyword)) {
    pagesFlow { page ->
      getOrNull<Document>("search?q=$keyword&p=$page")?.run {
        // <div class="im">
        //   <a href="/artifact/org.springframework/spring-web">
        //     <picture>..</picture>
        //   </a>
        // </div>
        select(".im > a").takeIf { it.isNotEmpty() }?.forEachConcurrently {
          resolveDependency(link = it.attr("href").removePrefix("/artifact/"))
        }
      }
    }
  }

  override fun fetchGroups(group: String): Flow<Dependency> = cache(Fetch.Group(group)) {
    fetchGroups(group, recursively = false)
  }

  override fun fetchStartsWith(startsWith: String): Flow<Dependency> = cache(Fetch.StartsWith(startsWith)) {
    fetchGroups(group = startsWith, recursively = true)
  }

  private fun fetchGroups(group: String, recursively: Boolean): Flow<Dependency> = pagesFlow { page ->
    getOrNull<Document>("artifact/$group?p=$page")?.run {
      // <p class="im-subtitle">
      //   <a href="org.scala-lang/scala-library">scala-library</a> // Artifact
      // </p>
      // <p class="im-subtitle">
      //   <a href="org.scala-lang.modules">org.scala-lang.modules</a> // Group
      // </p>
      select(".im-subtitle > a").takeIf { it.isNotEmpty() }?.forEachConcurrently {
        val link = it.attr("href")
        when {
          // Just a artifact
          link.contains('/') -> send(resolveDependency(link))
          // Collect nested artifacts
          recursively -> sendAll { fetchGroups(group = link, recursively = true) }
        }
      }
    }
  }

  private suspend fun resolveDependency(link: String): Dependency {
    var notation = link.replace('/', ':')

    // Some artifacts belong to the Scala version, so we need to get it exactly
    if (fetchExactly) getOrNull<Document>("artifact/$link")
      ?.selectFirst("#snippets table tbody a")
      ?.href()?.apply {
        // <a href="activity-compose/1.3.1" class="vbtn release">0.0.4</a>
        val group = link.substringBefore('/')
        val artifact = this.substringBefore('/')
        notation = "$group:$artifact"
      }

    return Dependency(notation)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is MvnRepositoryClient) return false
    if (!super.equals(other)) return false

    if (fetchExactly != other.fetchExactly) return false

    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode()
    result = 31 * result + fetchExactly.hashCode()
    return result
  }
}