import com.tfowl.ktor.client.features.JsoupFeature
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList


/**
 * @author 凛 (https://github.com/RinOrz)
 */
class DepsFetchTests : StringSpec({

  "internal client" {
    val client = createClient()
    client.artifactsHtml("---").shouldBeNull()
    client.artifactsHtml("org.jetbrains")?.body().toString().apply {
      shouldContain("<div class=\"im\">")
      shouldContain("<div class=\"im-header\">")
      shouldContain("<div class=\"im-description\">")
      shouldContain("<ul class=\"search-nav\">")
    }
  }

  "check page" {
    createClient().calculatePageCount("org").apply {
      shouldNotBe("Next")
      shouldBeInstanceOf<Int>()
    }
  }

  "fetch artifacts" {
    val client = createClient()

    client.fetchArtifactDeps("org.jetbrains", recursively = true)
      .filter { it.startsWith("org.jetbrains.jps") }
      .toList()
      .shouldNotBeEmpty()

//    client.fetchArtifactDeps("androidx", recursively = true)
//      .filter { it.startsWith("androidx.lifecycle") }
//      .toList()
//      .shouldBeEmpty()
  }
})