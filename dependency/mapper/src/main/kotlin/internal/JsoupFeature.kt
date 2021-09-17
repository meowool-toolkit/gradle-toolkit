@file:Suppress("unused")

package com.meowool.gradle.toolkit.internal

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
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
