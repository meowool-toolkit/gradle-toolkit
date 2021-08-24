@file:Suppress("SpellCheckingInspection")

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.asFlow

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class DepsGenerateTests : StringSpec({
  fun ClassWriter.add(meta: DependencyMeta) {
    if (meta.children.isNotEmpty()) {
      val innerClass = this.innerClass(meta.name)
      meta.children.forEach { innerClass.add(it) }
    }
    // Add notation as a field
    meta.notation?.let { this.field(meta.name, "$it:_") }
  }

  fun Class<*>.debugging(): String = buildString {
    append(simpleName)
    if (declaredClasses.isNotEmpty() || declaredFields.isNotEmpty()) {
      appendLine(" {")
      appendLine(buildString inner@{
        declaredFields.forEachIndexed { index, field ->
          this@inner.append("val " + field.name)
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
    val libs = ClassWriter(name = "Libs")
    """
      a:b
      foo:bar
      foo:bar-baz
    """.lines().asFlow().flattenToDepTree().forEach(libs::add)
    libs.make().load(null).loaded.debugging() shouldBe """
      Libs {
        A {
          val B
        }
        Foo {
          val Bar
          Bar {
            val Baz
          }
        }
      }
    """.trimIndent()
  }

  "complex" {
    val libs = ClassWriter(name = "Libs")

    """
      com.typesafe.akka:akka-actor
      com.google.inject.extensions:guice-dagger-adapter
      com.google.inject:jdk8-tests
      com.google.inject.integration:integration
      com.google.inject:multibindings
    """.lines().asFlow().flattenToDepTree().forEach(libs::add)
    libs.make().load(null).loaded.debugging() shouldBe """
      Libs {
        Com {
          Typesafe {
            Akka {
              val Actor
            }
          }
          Google {
            Inject {
              val Integration
              val Multibindings
              Extensions {
                Guice {
                  Dagger {
                    val Adapter
                  }
                }
              }
              Jdk8 {
                val Tests
              }
            }
          }
        }
      }
    """.trimIndent()
  }
})