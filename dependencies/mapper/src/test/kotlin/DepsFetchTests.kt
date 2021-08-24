import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.features.logging.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList


/**
 * @author 凛 (https://github.com/RinOrz)
 */
class DepsFetchTests : StringSpec({
  val client = createClient()

  "check page" {
    client.calculatePageCount("org").apply {
      shouldNotBe("Next")
      shouldBeInstanceOf<Int>()
    }
  }

  "fetch artifacts" {
    client.fetchArtifacts("org.jetbrains", recursively = false)
      .filter { it.startsWith("org.jetbrains.jps") }
      .toList()
      .shouldBeEmpty()

    client.fetchArtifacts("org.jetbrains", recursively = true)
      .filter { it.startsWith("org.jetbrains.jps") }
      .toList()
      .shouldNotBeEmpty()
  }
})