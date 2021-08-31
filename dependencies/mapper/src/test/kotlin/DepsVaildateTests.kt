@file:Suppress("SpellCheckingInspection")

import MappedClassesFactory.Companion.validDependency
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.collect

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class DepsVaildateTests : FreeSpec({

  "check remote dependencies" {
    suspend fun DependencyRepositoryClient.test() = fetchGroups("com.squareup.okhttp3").collect { dep ->
      dep.count { it == ':' } shouldBe 1
    }
    createMvnClient().test()
    createCentralClient().test()
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
    ) { validDependency("com.squareup.okhttp3:okhttp:_") }

    shouldThrowMessage(
      "`com.squareup.okhttp3: okhttp` cannot be has spaces."
    ) { validDependency("com.squareup.okhttp3: okhttp") }

    shouldNotThrowAny { validDependency(" com.squareup.okhttp3:okhttp ") }
  }
})