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
package com.meowool.gradle.toolkit.internal.client

import com.meowool.gradle.toolkit.LibraryDependency
import com.meowool.gradle.toolkit.internal.ConcurrentScope
import com.meowool.gradle.toolkit.internal.DefaultJson
import com.meowool.gradle.toolkit.internal.concurrentFlow
import com.meowool.gradle.toolkit.internal.retryConnection
import com.meowool.sweekt.coroutines.flowOnIO
import com.tfowl.ktor.client.features.JsoupFeature
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import net.andreinc.mockneat.unit.networking.IPv4s
import net.andreinc.mockneat.unit.networking.IPv6s
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Common repository client abstract class.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal abstract class DependencyRepositoryClient(
  internal val baseUrl: String,
  private val logLevel: HttpLoggingInterceptor.Level,
) : AutoCloseable {
  private var client: HttpClient? = null

  abstract fun fetch(keyword: String): Flow<LibraryDependency>

  abstract fun fetchGroups(group: String): Flow<LibraryDependency>

  open fun fetchPrefixes(startsWith: String): Flow<LibraryDependency> = fetch(startsWith)
    .filter { it.startsWith(startsWith) }

  suspend inline fun <reified T> get(url: String): T = (client ?: createClient()).get("$baseUrl/${url.replace("//", "/")}")

  suspend inline fun <reified T> getOrNull(url: String): T? = try {
    get(url)
  } catch (e: ClientRequestException) {
    null
  }

  protected fun pagesFlow(
    initial: Int = 1,
    block: suspend ConcurrentScope<LibraryDependency>.(Int) -> Any?
  ): Flow<LibraryDependency> = concurrentFlow {
    var page = initial
    while (true) {
      block(page++) ?: break
    }
  }.retryConnection().flowOnIO()

  private fun createClient() = HttpClient(OkHttp) {
    install(JsoupFeature)
    install(JsonFeature) {
      serializer = KotlinxSerializer(DefaultJson)
    }
    engine { addInterceptor(HttpLoggingInterceptor().setLevel(logLevel)) }
    defaultRequest { header("x-forwarded-for", randomIP()) }
  }.also { client = it }

  private fun randomIP() = when ((0..1).random()) {
    0 -> IPv4s.ipv4s().get()
    else -> IPv6s.ipv6s().get()
  }

  override fun close() {
    client?.close()
    client = null
  }
}
