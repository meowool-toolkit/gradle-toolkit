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
import com.meowool.gradle.toolkit.internal.DefaultJson
import com.meowool.gradle.toolkit.internal.flatMapConcurrently
import io.kotest.core.spec.style.StringSpec
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
