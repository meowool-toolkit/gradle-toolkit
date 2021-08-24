@file:Suppress("SpellCheckingInspection")

import extension.RootGradleDslExtension
import org.gradle.api.Project
import org.gradle.api.initialization.Settings

/**
 * Use the pre-built dependency mapper configuration.
 *
 * @see dependencyMapper
 */
fun Project.dependencyMapperPrebuilt() = dependencyMapper { builtIn() }

/**
 * Use the pre-built dependency mapper configuration.
 *
 * @see dependencyMapper
 */
fun Settings.dependencyMapperPrebuilt() = dependencyMapper { builtIn() }

/**
 * Use the pre-built dependency mapper configuration.
 *
 * @see dependencyMapper
 */
fun RootGradleDslExtension.dependencyMapperPrebuilt() = rootProject.dependencyMapper { builtIn() }

internal fun <T: DependencyMapperConfiguration> T.builtIn() = apply {
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
        .replace("constraintlayout", "ConstraintLayout")
        .replace("viewbinding", "ViewBinding")
        .replace("databinding", "DataBinding")
        .replace("bundletool", "BundleTool")
        .replace("signflinger", "SignFlinger")
        .replace("zipflinger", "ZipFlinger")
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
    it
      .replace("org.jetbrains.kotlin", "kotlin")
      .replace("org.jetbrains.ktor", "ktor")
      .replace("org.jetbrains.intellij", "intellij")
      .replace("com.google.android", "google")
      .replace("org.chromium.net", "chromium")
      .replace("com.google.auto.service", "google")
      .replace("com.squareup", "square")
      .replace("gradle.dsl.x", "GradleDslX")
      .replace("app.cash", "CashApp")
      .replace("org.junit.jupiter", "")
      .replace("-kt", "")
      .removePrefix("com.github.ajalt.")
      .removePrefix("com.linkedin.")
      .removePrefix("com.afollestad.")
      .removePrefix("me.liuwj.")
      .removePrefix("com.")
      .removePrefix("cn.")
      .removePrefix("org.")
      .removePrefix("io.github.")
      .removePrefix("io.")
      .removePrefix("me.")
  }
  addMvnDependencies(
    "androidx",
    "com.android",
    "com.google",
    "com.rinorz",
    "com.meowool",
    "com.umeng",
    "io.insert-koin",
    "com.squareup",
    "org.jetbrains",
    "org.apache",
    "commons-io",
    "commons-logging",
    "io.coil-kt",
    "io.arrow-kt",
    "app.cash",
    "com.didiglobal",
    "com.yalantis",
    "com.tencent",
    "com.firebase",
    "com.jakewharton",
    "com.airbnb",
    "com.facebook",
    "io.github.javaeden.orchid",
    "org.robolectric",
    "org.springframework",
    "org.junit",
    "io.kotest",
    "org.spekframework.spek2",
    "io.strikt",
    "io.mockk",
    "org.mockito",
    "org.smali",
    "org.jsoup",
    "org.javassist",
    "org.conscrypt",
    "mysql",
    "com.afollestad",
    "com.linkedin.dexmaker",
    "org.ow2",
    "net.bytebuddy",
    "me.liuwj.ktorm",
    "org.yaml",
    "com.github.ajalt.clikt",
  )
  addDependenciesMapping(
    "com.github.promeg:tinypinyin" to "TinyPinyin",
    "de.fayard.refreshVersions:refreshVersions" to "RefreshVersions",
    "com.github.donkingliang:ConsecutiveScroller" to "ConsecutiveScroller",
    "org.zeroturnaround:zt-zip" to "ZtZip",
    "com.andkulikov:transitionseverywhere" to "TransitionsEverywhere",
    "in.arunkumarsampath:transition-x" to "TransitionX",
  )
}