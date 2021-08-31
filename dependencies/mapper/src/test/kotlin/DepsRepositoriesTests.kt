@file:Suppress("SpellCheckingInspection")

import com.meowool.sweekt.coroutines.contains
import com.meowool.sweekt.coroutines.flowOnIO
import com.meowool.sweekt.coroutines.size
import com.meowool.sweekt.iteration.contains
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.scopes.FreeSpecContainerContext
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.jsoup.nodes.Document


/**
 * @author 凛 (https://github.com/RinOrz)
 */
class DepsRepositoriesTests : FreeSpec({
  "from google maven repository" - {
    val google = createGoogleClient()
    "fetch not found" {
      google.getOrNull<Document>("abcdefg/group-index.xml").shouldBeNull()
    }
    "fetch all" {
      google.fetchAllDependencies()
        .filter { it.equals("androidx.annotation:annotation-experimental-lint") }
        .flowOnIO().size() shouldBe 1
    }
    "fetch by group" {
      val groups = flowOf("androidx", "com.android")
      groups.flatMapConcurrently { google.fetchGroups(it) }
        .filterNot { dep -> groups.toList().any { dep.group == it } }
        .flowOnIO()
        .size()
        .shouldBeZero()
    }
    "fetch by `startsWith`" {
      val starts = flowOf("androidx", "com.android")
      starts.flatMapConcurrently { google.fetchStartsWith(it) }
        .flowOnIO()
        .filterNot { dep -> starts.toList().any { dep.startsWith(it) } }
        .size()
        .shouldBeZero()
    }
  }

  suspend fun FreeSpecContainerContext.commonTest(client: DependencyRepositoryClient) {
    "fetch by keyword" {
      client.fetch("javassist").flowOnIO().toList().apply {
        this shouldContain Dependency("org.javassist:javassist")
        if (client is MvnRepositoryClient) {
          this shouldContain Dependency("com.googlecode.jmapper-framework:jmapper-core")
          this shouldContain Dependency("jaop.domain:domain")
        }
      }
      client.fetch("coil").flowOnIO().size() shouldBeGreaterThanOrEqual if (client is MvnRepositoryClient) 26 else 19
    }
    "fetch by group" {
      val groups = flowOf("io.reactivex", "org.scala-lang")
      groups.flatMapConcurrently { client.fetchGroups(it) }.flowOnIO().toList().apply {
        // Contains
        if (client is MvnRepositoryClient) {
          this shouldContain Dependency("org.scala-lang:scala3-library")
        } else {
          this shouldContain Dependency("org.scala-lang:scala3-library_3")
        }
        this shouldContain Dependency("io.reactivex:rxjava")
        // Not contains
        contains { it.startsWith("org.scala-lang.modules") }.shouldBeFalse()
        contains { it.startsWith("io.reactivex.rxjava2") }.shouldBeFalse()
        // All groups are matched
        all { groups.contains { g -> g == it.group } }.shouldBeTrue()
      }
    }
    "fetch by `startsWith`" {
      flowOf("io.reactivex", "org.scala-lang")
        .flatMapConcurrently { client.fetchStartsWith(it) }
        .flowOnIO().toList().apply {
          forEach { println(it) }
          if (client is MvnRepositoryClient) {
            this shouldContain Dependency("org.scala-lang.modules:scala-collection-compat")
          } else {
            this shouldContain Dependency("org.scala-lang.modules:scala-collection-compat_3")
          }
          this shouldContain Dependency("io.reactivex.rxjava2:rxjava")
          this.all { it.startsWith("io.reactivex") || it.startsWith("org.scala-lang") }
        }
    }
  }

  "from mvn repository" - {
    commonTest(createMvnClient(fetchExactly = false))
    "fetch exactly by group" {
      createMvnClient(fetchExactly = true).fetchGroups("org.scala-lang.modules").flowOnIO().toList().also {
        it shouldContain Dependency("org.scala-lang.modules:scala-collection-compat_3")
        it shouldContain Dependency("org.scala-lang.modules:scala-xml_3")
        it shouldNotContain Dependency("org.scala-lang.modules:scala-xml")
      }
    }
  }

  "from maven repository" - {
    commonTest(createCentralClient())
  }

  "merge" {
//      val client = MavenCentralClient()
//      listOf(
//        "commons-io",
//        "commons-logging",
//
//        "app.cash",
//        "net.mamoe",
//        "net.bytebuddy",
//        "me.liuwj.ktorm",
//
//        "com.umeng",
//        "com.airbnb",
//        "com.google",
//        "com.rinorz",
//        "com.tencent",
//        "com.meowool",
//        "com.firebase",
//        "com.facebook",
//        "com.squareup",
//        "com.yalantis",
//        "com.facebook",
//        "com.afollestad",
//        "com.didiglobal",
//        "com.jakewharton",
//        "com.linkedin.dexmaker",
//        "com.github.ajalt.clikt",
//
//        "org.ow2",
//        "org.junit",
//        "org.smali",
//        "org.jsoup",
//        "org.mockito",
//        "org.jetbrains",
//        "org.javassist",
//        "org.conscrypt",
//        "org.robolectric",
//        "org.springframework",
//        "org.spekframework.spek2",
//
//        "org.apache.tika",
//        "org.apache.hbase",
//        "org.apache.hadoop",
//        "org.apache.commons",
//        "org.apache.logging.log4j",
//
//        "io.ktor",
//        "io.mockk",
//        "io.kotest",
//        "io.strikt",
//        "io.coil-kt",
//        "io.arrow-kt",
//        "io.insert-koin",
//        "io.github.reactivecircus",
//        "io.github.javaeden.orchid",
//      ).asFlow().flatMapConcurrently {
//        client.fetchStartsWith(it)
//      }.flowOnIO().size().also { println(it) }
    channelFlow {
      val client = MavenCentralClient()
      listOf(
        "commons-io",
        "commons-logging",

        "app.cash",
        "net.mamoe",
        "net.bytebuddy",
        "me.liuwj.ktorm",

        "com.umeng",
        "com.airbnb",
        "com.google",
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
        "com.linkedin.dexmaker",
        "com.github.ajalt.clikt",

        "org.ow2",
        "org.junit",
        "org.smali",
        "org.jsoup",
        "org.mockito",
        "org.jetbrains",
        "org.javassist",
        "org.conscrypt",
        "org.robolectric",
        "org.springframework",
        "org.spekframework.spek2",

        "org.apache.tika",
        "org.apache.hbase",
        "org.apache.hadoop",
        "org.apache.commons",
        "org.apache.logging.log4j",

        "io.ktor",
        "io.mockk",
        "io.kotest",
        "io.strikt",
        "io.coil-kt",
        "io.arrow-kt",
        "io.insert-koin",
        "io.github.reactivecircus",
        "io.github.javaeden.orchid",
      ).forEach {
        launch {
          client.fetchStartsWith(it).collect {
            launch { send(it) }
          }
        }
      }
    }.flowOnIO().size().also { println(it) }
  }

})