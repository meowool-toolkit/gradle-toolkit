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
 *
 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
package com.meowool.gradle.toolkit.internal.client

import com.meowool.gradle.toolkit.LibraryDependency
import com.meowool.gradle.toolkit.PluginId
import com.meowool.gradle.toolkit.internal.DependencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.nodes.Document

/**
 * A http client of [DependencyRepository.GradlePluginPortal].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal class GradlePluginClient(
  logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE,
) : DependencyRepositoryClient(baseUrl = "https://plugins.gradle.org", logLevel) {

  override fun fetch(keyword: String): Flow<LibraryDependency> = pagesFlow(initial = 0) { page ->
    get<Document>("search?term=$keyword&page=$page")
      .select(".plugin-id")
      .takeIf { it.isNotEmpty() }
      ?.forEachConcurrently { send(PluginId(it.text()).toLibraryDependency()) }
  }

  override fun fetchGroups(group: String): Flow<LibraryDependency> = fetch(group).filter { it.group == group }

// FIXME: It's too slow..
//  override fun fetchStartsWith(startsWith: String): Flow<LibraryDependency> = cache(Fetch.StartsWith(startsWith)) {
//    channelFlow {
//      suspend fun impl(url: String) {
//        val list = getOrNull<Document>(url)
//          ?.select("pre > a")
//          ?.map { it.text() }
//          ?.takeIf { it.isNotEmpty() } ?: return
//
//        if (list.any { it == "maven-metadata.xml" }) launch {
//          // Is a plugin path
//          val metadata = get<Document>("$url/maven-metadata.xml")
//          val versions = metadata.select("versions > version").map { it.text() }
//          launch {
//            send(LibraryDependency(
//              group = metadata.selectFirst("groupId")!!.text(),
//              artifact = metadata.selectFirst("artifactId")!!.text()
//            ))
//          }
//          // Exclude version directories and then recursive other subpaths
//          list.filter { versions.contains(it).not() && it.endsWith('/') }.forEachConcurrently { impl("$url/$it") }
//        } else {
//          // Recursive subpaths
//          list.filter { it.endsWith('/') }.forEachConcurrently { impl("$url/$it") }
//        }
//      }
//
//      impl("m2/" + startsWith.replace('.', '/'))
//    }.retryConnection()
//  }
}
