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
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
@file:Suppress("SpellCheckingInspection")

package com.meowool.gradle.toolkit.internal.client

import com.meowool.gradle.toolkit.LibraryDependency
import com.meowool.gradle.toolkit.internal.DependencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.logging.HttpLoggingInterceptor

/**
 * A http client of [DependencyRepository.MavenCentral].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal class MavenCentralClient(
  logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE
) : DependencyRepositoryClient(baseUrl = "https://search.maven.org", logLevel) {

  override fun fetch(keyword: String): Flow<LibraryDependency> = fetchImpl(keyword)

  override fun fetchGroups(group: String): Flow<LibraryDependency> = fetchImpl("g:\"$group\"")

  private fun fetchImpl(keyword: String): Flow<LibraryDependency> = pagesFlow { page ->
    val offset = (page - 1) * 1000
    // Only get up to 1000 results at a time to avoid excessive server pressure
    get<Search>("solrsearch/select?q=$keyword&start=$offset&rows=1000").result.dependencies
      .takeIf { it.isNotEmpty() }
      ?.forEachConcurrently { send(LibraryDependency(it.toString())) }
  }

  @Serializable private data class Search(@SerialName("response") val result: Result) {
    @Serializable data class Result(@SerialName("docs") val dependencies: List<Notation>) {
      @Serializable data class Notation(@SerialName("g") val group: String, @SerialName("a") val artifact: String) {
        override fun toString(): String = "$group:$artifact"
      }
    }
  }
}
