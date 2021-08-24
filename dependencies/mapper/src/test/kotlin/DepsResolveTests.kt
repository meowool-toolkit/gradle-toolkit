@file:Suppress("SpellCheckingInspection")

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class DepsResolveTests : FreeSpec({
  val client = createClient()

  "valid dependencies" {
    client.fetchArtifactDeps("com.squareup.okhttp3", recursively = false).collect { dep ->
      dep.count { it == ':' } shouldBe 1
    }
  }

  "dependency simplify" - {
    "foo.bar.gav:gav" {
      val dep = testCase.displayName
      val group = dep.substringBefore(':')
      val name = dep.substringAfter(':')

      group.removeSuffix(name) shouldBe "foo.bar."
    }

    "foo.bar.gav:gav-" {
      fun String.normalize(): String {
        val notation = this.replace('-', '.').replace('_', '.')
        var group = notation.substringBefore(':')
        val name = notation.substringAfter(':')

        var nameSplit = name.split('.')
        while (nameSplit.isNotEmpty()) {
          group = group.removeSuffix(nameSplit.joinToString("."))
          nameSplit = nameSplit.dropLast(1)
        }

        return group + name
      }

      "foo.bar.gav:gav".normalize() shouldBe "foo.bar.gav"
      "${testCase.displayName}test".normalize() shouldBe "foo.bar.gav.test"
      "foo.bac-test:bac-test".normalize() shouldBe "foo.bac.test"
      "foo.bac-test:bac-test-core".normalize() shouldBe "foo.bac.test.core"
    }
  }

  "valid dependency notation" {
    shouldThrowMessage(
      "`com.squareup.okhttp3:okhttp:_` can only has one `:` symbol used to separate `group` and `artifact`, in other words, " +
        "the notation cannot contain the `artifact` version."
    ) { flowOf("com.squareup.okhttp3:okhttp:_").flattenToDepTree() }

    shouldThrowMessage(
      "`com.squareup.okhttp3: okhttp` cannot be has spaces."
    ) { flowOf("com.squareup.okhttp3: okhttp").flattenToDepTree() }

    shouldNotThrowAny { flowOf(" com.squareup.okhttp3:okhttp ").flattenToDepTree() }
  }

  "flatten dep-tree" {
    val tree = flowOf(
      "com.compose.ui:ui ",
      "com.compose.material:material",
      "com.compose.material:material-core",
      "com.ads:identifier",
      "google.webkit:core",
    ).flattenToDepTree()

    tree.first { it.name == "Com" }.apply {
      notation.shouldBeNull()

      children.forEach {
        when (it.name) {
          "Ads" -> {
            it.children.apply {
              shouldHaveSize(1)
              first().notation shouldBe "com.ads:identifier"
              first().children.shouldBeEmpty()
            }
          }
          else -> {
            it.name shouldBe "Compose"
            it.notation.shouldBeNull()
            it.children.apply {
              shouldHaveSize(2)
              forEach { compose ->
                compose.name shouldBeIn arrayOf("Ui", "Material")
                compose.notation shouldBeIn arrayOf(
                  "com.compose.ui:ui",
                  "com.compose.material:material"
                )
              }
            }
          }
        }
      }
    }
  }

  "flatten and format" {
    val formatter = DepFormatter(
      replaceNotation = {
        it.replace("com.tt", "main")
          .replace("com.faa.bar", "main.bar")
      },
      replaceName = {
        it.replace("ui", "userInterface")
          .replace("bar-tt", "bar")
      },
    )

    val tree = flowOf(
      "tt.ui:ui",
      "tt.add:add-wwb",
      "faa.bar:bar-tt",
    ).flattenToDepTree(formatter)

    val main = tree.first()

    tree shouldHaveSize 2
    main.name shouldBe "Main"
  }
})