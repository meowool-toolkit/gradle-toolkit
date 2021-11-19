import com.meowool.gradle.toolkit.internal.DefaultJson
import com.meowool.gradle.toolkit.internal.flatMapConcurrently
import com.tfowl.ktor.client.features.JsoupFeature
import io.kotest.core.spec.style.StringSpec
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.userAgent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import okhttp3.logging.HttpLoggingInterceptor
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName


/**
 * @author 凛 (https://github.com/RinOrz)
 */
class FastResultSizeTests : StringSpec({
  "maven" {
    val client = HttpClient {
      install(JsonFeature) {
        serializer = KotlinxSerializer(DefaultJson)
      }
    }
    flowOf(
      "app.cash",
      "net.mamoe",
      "net.bytebuddy",
      "me.liuwj.ktorm",

      // Apache
      "commons-io",
      "commons-logging",

      "org.apache.tika",
      "org.apache.hbase",
      "org.apache.hadoop",
      "org.apache.commons",
      "org.apache.logging.log4j",

      "com.umeng",
      "com.airbnb",
      "com.rinorz",
      "com.tencent",
      "com.meowool",
      "com.firebase",
      "com.facebook",
      "com.squareup",
      "com.yalantis",
      "com.facebook",
      "com.afollestad",
      "com.didiglobal",
      "com.jakewharton",
      "com.soywiz.korlibs",
      "com.linkedin.dexmaker",
      "com.github.ajalt.clikt",

      "org.ow2.asm",
      "org.junit",
      "org.smali",
      "org.jsoup",
      "org.mockito",
      "org.javassist",
      "org.conscrypt",
      "org.robolectric",
      "org.springframework",
      "org.spekframework.spek2",

      "io.ktor",
      "io.mockk",
      "io.kotest",
      "io.strikt",
      "io.coil-kt",
      "io.arrow-kt",
      "io.insert-koin",
      "io.github.reactivecircus",
      "io.github.javaeden.orchid",

      "org.jetbrains.markdown",
      "org.jetbrains.annotations",
      "org.jetbrains.kotlin",
      "org.jetbrains.kotlinx",
      "org.jetbrains.compose",
      "org.jetbrains.dokka",
      "org.jetbrains.exposed",
      "org.jetbrains.kotlin-wrappers",
      "org.jetbrains.intellij",
      "org.jetbrains.anko",
      "org.jetbrains.spek",
      "org.jetbrains.lets-plot",
      "org.jetbrains.skiko",
      "org.jetbrains.teamcity",
    ).flatMapConcurrently {
      flow {
        emit(client.get<Result>("https://search.maven.org/solrsearch/select?q=$it").response.size)
      }
    }.toList().sum().also(::println)
  }
}) {

  @Serializable
  data class Result(@SerialName("response") val response: Response) {
    @Serializable
    data class Response(@SerialName("numFound") val size: Int)
  }
}