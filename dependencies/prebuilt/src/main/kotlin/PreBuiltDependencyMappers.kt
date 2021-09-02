@file:Suppress("SpellCheckingInspection", "NAME_SHADOWING")

import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.configure

/**
 * Use the pre-built dependency mapper configuration.
 *
 * @see dependencyMapper
 */
fun Project.dependencyMapperPrebuilt() = extensions.configure<DependencyMapperExtension> { prebuilt() }

/**
 * Use the pre-built dependency mapper configuration.
 *
 * @see dependencyMapper
 */
fun Settings.dependencyMapperPrebuilt() = dependencyMapper { prebuilt() }

/**
 * Use the pre-built dependency mapper configuration.
 *
 * @see dependencyMapper
 */
fun GradleToolkitExtension.dependencyMapperPrebuilt() = dependencyMapper { prebuilt() }

internal fun <T: DependencyMapperExtension> T.prebuilt() = apply {
  mapDependencies(
    "com.tfowl.ktor:ktor-jsoup" to "Ktor.Jsoup",
    "com.github.ben-manes.caffeine:caffeine" to "Caffeine",
    "com.github.promeg:tinypinyin" to "TinyPinyin",
    "de.fayard.refreshVersions:refreshVersions" to "RefreshVersions",
    "com.github.donkingliang:ConsecutiveScroller" to "ConsecutiveScroller",
    "org.zeroturnaround:zt-zip" to "ZtZip",
    "com.andkulikov:transitionseverywhere" to "TransitionsEverywhere",
    "in.arunkumarsampath:transition-x" to "TransitionX",
    "com.diffplug.spotless:spotless-plugin-gradle" to "Gradle.Spotless",
    "com.gradle.publish:plugin-publish-plugin" to "Gradle.Publish.Plugin"
  )
  remoteDependencies {
    kotlin()
    groups(
      "mysql",
      "org.yaml",
      repositories = { mavenCentral() }
    )
    startsWith(
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
      repositories = { mavenCentral() },
      // Skip deprecated dependencies
      filter = { it.startsWith("com.meowool.toolkit:gradle-dsl-x").not() }
    )
    startsWith(
      "android",
      "androidx",
      "com.android",
      repositories = { google() },
      // Skip deprecated dependencies
      filter = { it.startsWith("com.android.support").not() }
    )
  }
  capitalizeFirstLetter {
    (it.contains("ios", ignoreCase = true)
      || it.contains("wasm32", ignoreCase = true)
      || it.contains("wasm64", ignoreCase = true)).not()
  }
  transformName {
    when (it) {
      // Exact replacement
      "Io" -> "IO"
      "ios", "IOS" -> "iOS"
      else -> it
        .replace("androidx", "AndroidX")
        .replace("kotlinx", "KotlinX")
        .replace("viewmodel", "ViewModel")
        .replace("uiautomator", "UiAutomator")
        .replace("janktesthelper", "JanktestHelper")
        .replace("constraintlayout", "ConstraintLayout")
        .replace("viewbinding", "ViewBinding")
        .replace("databinding", "DataBinding")
        .replace("bundletool", "BundleTool")
        .replace("signflinger", "SignFlinger")
        .replace("zipflinger", "ZipFlinger")
        .replace("vectordrawable", "VectorDrawable")
        .replace("mingwx", "Mingw.X")
        .replace("mingwarm", "Mingw.Arm")
        .replace("linuxx", "Linux.X")
        .replace("linuxarm", "Linux.Arm")
        .replace("macosx", "MacOS.X")
        .replace("macosarm", "MacOS.Arm")
        .replace("iosx", "iOS.X")
        .replace("iosarm", "iOS.Arm")
        .replace("tvosx", "TvOS.X")
        .replace("tvosarm", "TvOS.Arm")
        .replace("wasm32", "wArm32")
        .replace("wasm64", "wArm64")
        .replace("watchosx", "WatchOS.X")
        .replace("watchosarm", "WatchOS.Arm")
        .replace("okhttp", "OkHttp")
        .replace("bytebuddy", "ByteBuddy")
    }
  }
  transformNotation {
    val it = it
      .replace("org.jetbrains.kotlin", "kotlin")
      .replace("org.jetbrains.intellij", "intellij")
      .replace("com.google.android", "google")
      .replace("org.chromium.net", "chromium")
      .replace("com.squareup", "square")
      .replace("app.cash", "CashApp")
      .replace("io.coil-kt", "Coil")
      .replace("io.arrow-kt", "Arrow")
      .removePrefix("com.github.ajalt.")
      .removePrefix("com.linkedin.")
      .removePrefix("com.afollestad.")
      .removePrefix("me.liuwj.")
      .removePrefix("io.github.")
    when {
      it.startsWith("com.", ignoreCase = true) -> it.removePrefixGreedy("com.")
      it.startsWith("net.", ignoreCase = true) -> it.removePrefixGreedy("net.")
      it.startsWith("cn.", ignoreCase = true) -> it.removePrefixGreedy("cn.")
      it.startsWith("org.", ignoreCase = true) -> it.removePrefixGreedy("org.")
      it.startsWith("top.", ignoreCase = true) -> it.removePrefixGreedy("top.")
      it.startsWith("edu.", ignoreCase = true) -> it.removePrefixGreedy("edu.")
      it.startsWith("ink.", ignoreCase = true) -> it.removePrefixGreedy("ink.")
      it.startsWith("info.", ignoreCase = true) -> it.removePrefixGreedy("info.")
      it.startsWith("pro.", ignoreCase = true) -> it.removePrefixGreedy("pro.")
      it.startsWith("io.", ignoreCase = true) -> it.removePrefixGreedy("io.")
      it.startsWith("me.", ignoreCase = true) -> it.removePrefixGreedy("me.")
      else -> it
    }
  }
}

private fun String.removePrefixGreedy(prefix: String) =
  removePrefix(prefix).removePrefix(prefix.capitalize()).removePrefix(prefix.toUpperCase())