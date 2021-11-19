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
import io.ktor.http.userAgent
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

  suspend inline fun <reified T> get(url: String): T =
    (client ?: createClient()).get("$baseUrl/${url.replace("//", "/")}")

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
    defaultRequest {
      userAgent(randomAgent())
      header("x-forwarded-for", randomIP())
    }
  }.also { client = it }

  private fun randomIP() = when ((0..1).random()) {
    0 -> IPv4s.ipv4s().get()
    else -> IPv6s.ipv6s().get()
  }

  private fun randomAgent() = headers[(0..headers.lastIndex).random()]

  override fun close() {
    client?.close()
    client = null
  }

  private companion object {
    private val headers = arrayOf(
      "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36",
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36",
      "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:30.0) Gecko/20100101 Firefox/30.0",
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/537.75.14",
      "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Win64; x64; Trident/6.0)",
      "ozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11",
      "Opera/9.25 (Windows NT 5.1; U; en)",
      "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
      "Mozilla/5.0 (compatible; Konqueror/3.5; Linux) KHTML/3.5.5 (like Gecko) (Kubuntu)",
      "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.0.12) Gecko/20070731 Ubuntu/dapper-security Firefox/1.5.0.12",
      "Lynx/2.8.5rel.1 libwww-FM/2.14 SSL-MM/1.4.1 GNUTLS/1.2.9",
      "Mozilla/5.0 (X11; Linux i686) AppleWebKit/535.7 (KHTML, like Gecko) Ubuntu/11.04 Chromium/16.0.912.77 Chrome/16.0.912.77 Safari/535.7",
      "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:10.0) Gecko/20100101 Firefox/10.0 ",
    )
  }
}
