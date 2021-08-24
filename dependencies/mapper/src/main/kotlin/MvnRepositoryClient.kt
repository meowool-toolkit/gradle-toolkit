import com.meowool.sweekt.coroutines.flowOnIO
import com.meowool.sweekt.safetyValue
import com.tfowl.ktor.client.features.JsoupFeature
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import net.andreinc.mockneat.unit.networking.IPv4s
import net.andreinc.mockneat.unit.networking.IPv6s
import org.jsoup.nodes.Document

/**
 * A client for [MvnRepository](https://mvnrepository.com/) website.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class MvnRepositoryClient(logLevel: LogLevel = LogLevel.NONE): AutoCloseable {
  private val client = HttpClient(OkHttp) {
    install(Logging) {
      logger = Logger.DEFAULT
      level = logLevel
    }
    install(JsoupFeature)
    defaultRequest { header("x-forwarded-for", randomIP()) }
  }

  fun fetchArtifacts(group: String, recursively: Boolean = true): Flow<String> = flow {
    @Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
    @OptIn(ExperimentalCoroutinesApi::class)
    emitAll(
      (1..calculatePageCount(group))
        .map { page -> fetchArtifactsByPage(group, recursively, page) }
        .merge()
    )
  }.flowOnIO()

  internal suspend fun calculatePageCount(group: String): Int = artifactsHtml(group)
    .selectFirst(".search-nav")
    .let { safetyValue { it.child(it.childrenSize() - 2)  } }
    ?.text()
    ?.toInt() ?: 1

  private fun fetchArtifactsByPage(
    group: String,
    recursively: Boolean,
    page: Int,
  ): Flow<String> = flow {
    artifactsHtml(group, page).run {
      getElementsByClass("im").forEach {
        // artifact or group name
        val name = it.selectFirst(".im-subtitle > a").text()
        when {
          // collect nested artifacts
          recursively && it.select(".im-description .b").isNotEmpty() -> emitAll(
            fetchArtifactsByPage(
              group = name,
              recursively = true,
              page = 1,
            )
          )
          else -> emit("$group:$name")
        }
      }
    }
  }

  private suspend fun artifactsHtml(group: String, page: Int = 1): Document =
    client.get("$baseUrl/artifact/$group?p=$page")

  private fun randomIP() = when((0..1).random()) {
    0 -> IPv4s.ipv4s().get()
    else -> IPv6s.ipv6s().get()
  }

  override fun close() = client.close()

  companion object {
    private const val baseUrl: String = "https://mvnrepository.com"
  }
}