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
 * 除如果您正在修改此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.meowool.gradle.toolkit.internal.client

import com.meowool.gradle.toolkit.LibraryDependency
import com.meowool.gradle.toolkit.internal.DependencyRepository
import com.meowool.gradle.toolkit.internal.forEachConcurrently
import com.meowool.gradle.toolkit.internal.href
import com.meowool.gradle.toolkit.internal.sendAll
import com.meowool.sweekt.substringBefore
import kotlinx.coroutines.flow.Flow
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.nodes.Document

/**
 * A http client of [DependencyRepository.MvnRepository].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal class MvnRepositoryClient(
  internal val fetchExactly: Boolean,
  logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE
) : DependencyRepositoryClient(baseUrl = "https://mvnrepository.com", logLevel) {

  override fun fetch(keyword: String): Flow<LibraryDependency> = cache(Fetch.Keyword(keyword)) {
    pagesFlow { page ->
      getOrNull<Document>("search?q=$keyword&p=$page")?.run {
        // <div class="im">
        //   <a href="/artifact/org.springframework/spring-web">
        //     <picture>..</picture>
        //   </a>
        // </div>
        select(".im > a").takeIf { it.isNotEmpty() }?.forEachConcurrently {
          send(resolveDependency(link = it.attr("href").removePrefix("/artifact/")))
        }
      }
    }
  }

  override fun fetchGroups(group: String): Flow<LibraryDependency> = cache(Fetch.Group(group)) {
    fetchGroups(group, recursively = false)
  }

  override fun fetchStartsWith(startsWith: String): Flow<LibraryDependency> = cache(Fetch.StartsWith(startsWith)) {
    fetchGroups(group = startsWith, recursively = true)
  }

  private fun fetchGroups(group: String, recursively: Boolean): Flow<LibraryDependency> = pagesFlow { page ->
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
          // Just an artifact
          link.contains('/') -> send(resolveDependency(link))
          // Collect nested artifacts
          recursively -> sendAll { fetchGroups(group = link, recursively = true) }
        }
      }
    }
  }

  private suspend fun resolveDependency(link: String): LibraryDependency {
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

    return LibraryDependency(notation)
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
