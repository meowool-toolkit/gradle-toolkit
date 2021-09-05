package com.meowool.gradle.toolkit.internal.client

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import com.meowool.gradle.toolkit.internal.Dependency
import com.meowool.gradle.toolkit.internal.sendList
import com.tfowl.ktor.client.features.JsoupFeature
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import net.andreinc.mockneat.unit.networking.IPv4s
import net.andreinc.mockneat.unit.networking.IPv6s
import java.util.concurrent.TimeUnit

/**
 * Common repository client abstract class.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal abstract class DependencyRepositoryClient(
  private val baseUrl: String,
  private val logLevel: LogLevel,
) : AutoCloseable {
  private var client: HttpClient? = null
  protected val cache: Cache<Fetch, List<Dependency>> = Caffeine.newBuilder()
    .expireAfter(object : Expiry<Fetch, List<Dependency>> {
      override fun expireAfterCreate(key: Fetch?, value: List<Dependency>?, currentTime: Long): Long = Long.MAX_VALUE

      override fun expireAfterUpdate(
        key: Fetch?,
        value: List<Dependency>?,
        currentTime: Long,
        currentDuration: Long,
      ): Long = Long.MAX_VALUE

      override fun expireAfterRead(
        key: Fetch?,
        value: List<Dependency>?,
        currentTime: Long,
        currentDuration: Long,
      ): Long = when (key) {
        // Big data, cache for 1 minute
        is Fetch.Any -> TimeUnit.MINUTES.toNanos(1)
        // Other small data can be fetched through the cached big data, so only need to be cached for 20 seconds
        else -> TimeUnit.SECONDS.toNanos(20)
      }
    }).build()

  abstract fun fetch(keyword: String): Flow<Dependency>

  abstract fun fetchGroups(group: String): Flow<Dependency>

  open fun fetchStartsWith(startsWith: String): Flow<Dependency> = channelFlow {
    suspend fun Dependency.send() = launch { send(this@send) }
    val cacheKey = Fetch.StartsWith(startsWith)
    cache.getIfPresent(cacheKey) ?: when (val allDependencies = cache.getIfPresent(Fetch.Any)) {
      // The cache of all dependencies is expired
      null -> fetch(startsWith).filter { it.startsWith(startsWith) }.onEach { it.send() }.toList()
      else -> allDependencies.filter { it.startsWith(startsWith) }.onEach { it.send() }
    }.also { cache.put(cacheKey, it) }
  }

  suspend inline fun <reified T> get(url: String): T = (client ?: createClient()).get("$baseUrl/$url")

  suspend inline fun <reified T> getOrNull(url: String): T? = try {
    get(url)
  } catch (e: ClientRequestException) {
    null
  }

  protected fun cache(key: Fetch, fetcher: suspend () -> Flow<Dependency>): Flow<Dependency> = channelFlow {
    sendList {
      cache.getIfPresent(key) ?: fetcher().toList().also { cache.put(key, it) }
    }
  }

  protected fun pagesFlow(block: suspend ProducerScope<Dependency>.(Int) -> Unit?): Flow<Dependency> = channelFlow {
    var page = 1
    while (true) {
      block(page++) ?: break
    }
  }.retry()

  private fun createClient() = HttpClient(OkHttp) {
    install(JsoupFeature)
    install(JsonFeature) {
      serializer = KotlinxSerializer(
        kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
      )
    }
    install(Logging) {
      logger = Logger.DEFAULT
      level = logLevel
    }
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