@file:OptIn(ExperimentalCoroutinesApi::class)

import com.meowool.sweekt.coroutines.flowOnIO
import com.meowool.sweekt.datetime.seconds
import com.meowool.sweekt.safetyValue
import com.tfowl.ktor.client.features.JsoupFeature
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.retry
import net.andreinc.mockneat.unit.networking.IPv4s
import net.andreinc.mockneat.unit.networking.IPv6s
import org.jsoup.nodes.Document

/**
 * A client for [MvnRepository](https://mvnrepository.com/) website.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal class MvnRepositoryClient(logLevel: LogLevel = LogLevel.NONE): AutoCloseable {
  private val client = HttpClient(OkHttp) {
    install(Logging) {
      logger = Logger.DEFAULT
      level = logLevel
    }
    install(JsoupFeature)
    defaultRequest { header("x-forwarded-for", randomIP()) }
  }

  suspend fun artifactsHtml(group: String, page: Int = 1): Document? = try {
    client.get("$BaseUrl/artifact/$group?p=$page")
  } catch (e: ClientRequestException) {
    null
  }

  fun fetchArtifactDeps(group: String, recursively: Boolean = true): Flow<String> = flow {
    emitAll(
      (1..calculatePageCount(group) + 1)
        .map { page -> fetchArtifactDepsByPage(group, recursively, page) }
        .merge()
    )
  }.retry(20) { delay(500); true }.flowOnIO()

  suspend fun calculatePageCount(group: String): Int = artifactsHtml(group)
    ?.selectFirst(".search-nav")
    ?.let { safetyValue { it.child(it.childrenSize() - 2) } }
    ?.text()?.toInt()
    ?: 2

  private fun fetchArtifactDepsByPage(
    group: String,
    recursively: Boolean,
    page: Int,
  ): Flow<String> = flow {
    artifactsHtml(group, page)?.run {
      getElementsByClass("im").forEach {
        // Artifact or group name
        val name = it.selectFirst(".im-subtitle > a").text()
        when {
          // Collect nested artifacts
          recursively && it.select(".im-description .b").isNotEmpty() -> emitAll(
            fetchArtifactDeps(group = name, recursively)
          )
          else -> emit("$group:$name")
        }
      }
    }
  }

  private fun randomIP() = when((0..1).random()) {
    0 -> IPv4s.ipv4s().get()
    else -> IPv6s.ipv6s().get()
  }

  override fun close() = client.close()

  companion object {
    private const val BaseUrl: String = "https://mvnrepository.com"
  }
}