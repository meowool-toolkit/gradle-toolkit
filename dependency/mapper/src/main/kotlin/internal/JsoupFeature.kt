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
@file:Suppress("unused")

package com.meowool.gradle.toolkit.internal

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.statement.HttpResponseContainer
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.AttributeKey
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

/**
 * [HttpClient] feature that deserializes response bodies into Jsoup [Document]
 * class using a provided [Parser]
 *
 * By default, [Html][ContentType.Text.Html] is deserialized using [Parser.htmlParser]
 * and [Xml][ContentType.Text.Xml] is deserialized using [Parser.xmlParser].
 *
 * Note: It will be only deserialized registered content types and for receiving
 * [Document] or superclasses.
 *
 * @property parsers Registered parsers for content types
 */
class JsoupFeature internal constructor(val parsers: Map<ContentType, Parser>) {

  /**
   * [JsoupFeature] configuration that is used during installation
   */
  class Config {

    /**
     * [Parsers][Parser] that will be used for each [ContentType]
     *
     * Default registered are:
     *  - Html [ContentType.Text.Html]
     *  - Xml [ContentType.Text.Xml]·[ContentType.Application.Xml]
     */
    val parsers: MutableMap<ContentType, Parser> = mutableMapOf(
      ContentType.Application.Xml to Parser.xmlParser(),
      ContentType.Text.Xml to Parser.xmlParser(),
      ContentType.Text.Html to Parser.htmlParser(),
    )
  }

  /**
   * The companion object for feature installation
   */
  companion object Feature : HttpClientFeature<Config, JsoupFeature> {
    override val key: AttributeKey<JsoupFeature> = AttributeKey("Jsoup")

    override fun prepare(block: Config.() -> Unit): JsoupFeature =
      JsoupFeature(Config().apply(block).parsers)

    override fun install(feature: JsoupFeature, scope: HttpClient) {
      scope.responsePipeline.intercept(HttpResponsePipeline.Receive) { (info, body) ->
        if (body !is ByteReadChannel)
          return@intercept

        if (!info.type.java.isAssignableFrom(Document::class.java))
          return@intercept

        val parser = feature.parsers
          .filterKeys { context.response.contentType()?.match(it) == true }
          .values.firstOrNull() ?: return@intercept

        val content = body.readRemaining().readText()

        val parsedBody = flow {
          emit(parser.parseInput(content, context.request.url.toString()))
        }.retry(20) {
          // In the case of high concurrency, some system errors may occur, so delay retrying
          delay(5)
          true
        }.first()

        proceedWith(HttpResponseContainer(info, parsedBody))
      }
    }
  }
}

/**
 * Install [JsoupFeature]
 */
@Suppress("FunctionName")
fun HttpClientConfig<*>.Jsoup(block: JsoupFeature.Config.() -> Unit = {}) {
  install(JsoupFeature, block)
}
