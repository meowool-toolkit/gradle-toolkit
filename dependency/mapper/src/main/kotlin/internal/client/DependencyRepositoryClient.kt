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
package com.meowool.gradle.toolkit.internal.client

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import com.meowool.gradle.toolkit.LibraryDependency
import com.meowool.gradle.toolkit.internal.DefaultJson
import com.meowool.gradle.toolkit.internal.JsoupFeature
import com.meowool.gradle.toolkit.internal.retryConnection
import com.meowool.gradle.toolkit.internal.sendList
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import net.andreinc.mockneat.unit.networking.IPv4s
import net.andreinc.mockneat.unit.networking.IPv6s
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

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
  protected val cache: Cache<Fetch, List<LibraryDependency>> = Caffeine.newBuilder()
    .expireAfter(object : Expiry<Fetch, List<LibraryDependency>> {
      override fun expireAfterCreate(
        key: Fetch?,
        value: List<LibraryDependency>?,
        currentTime: Long
      ): Long = Long.MAX_VALUE

      override fun expireAfterUpdate(
        key: Fetch?,
        value: List<LibraryDependency>?,
        currentTime: Long,
        currentDuration: Long,
      ): Long = Long.MAX_VALUE

      override fun expireAfterRead(
        key: Fetch?,
        value: List<LibraryDependency>?,
        currentTime: Long,
        currentDuration: Long,
      ): Long = when (key) {
        // Big data, cache for 1 minute
        is Fetch.Any -> TimeUnit.MINUTES.toNanos(1)
        // Other small data can be fetched through the cached big data, so only need to be cached for 20 seconds
        else -> TimeUnit.SECONDS.toNanos(20)
      }
    }).build()

  abstract fun fetch(keyword: String): Flow<LibraryDependency>

  abstract fun fetchGroups(group: String): Flow<LibraryDependency>

  open fun fetchStartsWith(startsWith: String): Flow<LibraryDependency> = channelFlow {
    suspend fun LibraryDependency.send() = launch { send(this@send) }
    val cacheKey = Fetch.StartsWith(startsWith)
    cache.getIfPresent(cacheKey) ?: when (val allDependencies = cache.getIfPresent(Fetch.Any)) {
      // The cache of all dependencies is expired
      null -> fetch(startsWith).filter { it.startsWith(startsWith) }.onEach { it.send() }.toList()
      else -> allDependencies.filter { it.startsWith(startsWith) }.onEach { it.send() }
    }.also { cache.put(cacheKey, it) }
  }

  suspend inline fun <reified T> get(url: String): T = (client ?: createClient()).get("$baseUrl/${url.replace("//", "/")}")

  suspend inline fun <reified T> getOrNull(url: String): T? = try {
    get(url)
  } catch (e: ClientRequestException) {
    null
  }

  protected fun cache(
    key: Fetch,
    fetcher: suspend () -> Flow<LibraryDependency>
  ): Flow<LibraryDependency> = channelFlow {
    sendList {
      cache.getIfPresent(key) ?: fetcher().toList().also { cache.put(key, it) }
    }
  }

  protected fun pagesFlow(
    initial: Int = 1,
    block: suspend ProducerScope<LibraryDependency>.(Int) -> Any?
  ): Flow<LibraryDependency> = channelFlow {
    var page = initial
    while (true) {
      block(page++) ?: break
    }
  }.retryConnection()

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

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is DependencyRepositoryClient) return false
    if (other.javaClass != this.javaClass) return false

    if (baseUrl != other.baseUrl) return false

    return true
  }

  override fun hashCode(): Int {
    var result = baseUrl.hashCode()
    result = 31 * result + javaClass.hashCode()
    return result
  }

  protected sealed class Fetch(val value: String?) {
    object Any : Fetch(null)
    class Keyword(value: String) : Fetch(value)
    class Group(value: String) : Fetch(value)
    class StartsWith(value: String) : Fetch(value)

    override fun equals(other: kotlin.Any?): Boolean {
      if (this === other) return true
      if (other !is Fetch) return false
      if (javaClass != other.javaClass) return false
      if (value != other.value) return false
      return true
    }

    override fun hashCode(): Int {
      var result = value?.hashCode() ?: 0
      result = 31 * result + javaClass.name.hashCode()
      return result
    }
  }
}
