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
import com.meowool.gradle.toolkit.internal.ConcurrentScope
import com.meowool.gradle.toolkit.internal.DependencyRepository
import com.meowool.gradle.toolkit.internal.concurrentFlow
import com.meowool.gradle.toolkit.internal.flatMapConcurrently
import com.meowool.gradle.toolkit.internal.retryConnection
import com.meowool.sweekt.coroutines.flowOnIO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.nodes.Document

/**
 * A http client of [DependencyRepository.Google].
 *
 * @author 凛 (RinOrz)
 */
internal class GoogleMavenClient(
  logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE,
) : DependencyRepositoryClient(baseUrl = "https://maven.google.com", logLevel) {

  override fun fetch(keyword: String): Flow<LibraryDependency> = fetchAllGroupIds()
    .flatMapConcurrently { fetchGroups(it) }

  override fun fetchGroups(group: String): Flow<LibraryDependency> = concurrentFlow {
    val groupPath = group.replace('.', '/')
    // <android.arch.core>
    //   <core/>
    //   <runtime/>
    // </android.arch.core>
    sendChildren(getOrNull("$groupPath/group-index.xml")) {
      LibraryDependency("$group:$it")
    }
  }.retryConnection()

  override fun fetchPrefixes(startsWith: String): Flow<LibraryDependency> = fetchAllGroupIds()
    .filter { it.startsWith(startsWith) }
    .flatMapConcurrently { fetchGroups(it) }

  private fun fetchAllGroupIds() = concurrentFlow<String> {
    // <metadata>
    //   <android.arch.core/>
    // </metadata>
    sendChildren(getOrNull("master-index.xml"))
  }.retryConnection().flowOnIO()

  private suspend inline fun <T> ConcurrentScope<T>.sendChildren(
    document: Document?,
    crossinline map: (String) -> T = @Suppress("UNCHECKED_CAST") { it as T },
  ) = document?.children()?.firstOrNull()?.children()?.forEachConcurrently { send(map(it.tagName())) }
}
