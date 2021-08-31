@file:Suppress("SpellCheckingInspection")

import com.meowool.sweekt.coroutines.size
import com.meowool.sweekt.iteration.size
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.collect

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class DepsGenerateTests : StringSpec({
  fun Class<*>.debugging(): String = buildString {
    append("class ")
    append(simpleName)
    if (declaredClasses.isNotEmpty() || declaredFields.isNotEmpty()) {
      appendLine(" {")
      appendLine(buildString inner@{
        declaredFields.forEachIndexed { index, field ->
          this@inner.append("val ")
          this@inner.append(field.name)
          this@inner.append(" = ")
          this@inner.append("\"${field.get(null)}\"")
          if (index < declaredFields.lastIndex) this@inner.appendLine()
        }
        if (declaredFields.isNotEmpty() && declaredClasses.isNotEmpty()) this@inner.appendLine()
        declaredClasses.forEachIndexed { index, clazz ->
          this@inner.append(clazz.debugging())
          if (index < declaredClasses.lastIndex) this@inner.appendLine()
        }
      }.prependIndent("  "))
      append("}")
    }
  }

  "simple" {
    val mapped = MappedClassesFactory.produce {
      arrayOf(
        "a:b",
        "foo:bar",
        "foo:bar-baz",
        "foo:bar_baz",
        "foo:bar.baz",
      ).forEach {
        map(it, DepFormatter.Default.format(it))
      }
    }

    mapped.load(null).loaded.debugging() shouldBe """
      class Libs {
        class A {
          val B = "a:b:_"
        }
        class Foo {
          val Bar = "foo:bar:_"
          class Bar {
            val Baz = "foo:bar-baz:_"
            val Baz1 = "foo:bar_baz:_"
            val Baz2 = "foo:bar.baz:_"
          }
        }
      }
    """.trimIndent()
  }

  "complex" {
    val mapped = MappedClassesFactory.produce {
      arrayOf(
        "com.typesafe.akka:akka-actor",
        "com.google.inject:jdk8-tests",
        "com.google.inject:multibindings",
        "com.google.inject.integration:integration",
        "com.google.inject.extensions:guice-dagger-adapter",
      ).forEach {
        map(it, DepFormatter.Default.format(it))
      }
    }

    mapped.load(null).loaded.debugging() shouldBe """
      class Libs {
        class Com {
          class Typesafe {
            class Akka {
              val Actor = "com.typesafe.akka:akka-actor:_"
            }
          }
          class Google {
            class Inject {
              val Multibindings = "com.google.inject:multibindings:_"
              val Integration = "com.google.inject.integration:integration:_"
              class Jdk8 {
                val Tests = "com.google.inject:jdk8-tests:_"
              }
              class Extensions {
                class Guice {
                  class Dagger {
                    val Adapter = "com.google.inject.extensions:guice-dagger-adapter:_"
                  }
                }
              }
            }
          }
        }
      }
    """.trimIndent()
  }

  "check remote mapped size" {
    fun Class<*>.collectFields() = Regex("val ").findAll(debugging()).size
    val dependencies = createCentralClient().fetch("compose")
    val mapped = MappedClassesFactory.produce {
      dependencies.collect { map(it, DepFormatter.Default.format(it)) }
    }
    mapped.load(null).loaded.collectFields() shouldBe dependencies.size()
  }
})