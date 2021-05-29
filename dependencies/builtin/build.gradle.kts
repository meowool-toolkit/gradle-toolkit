@file:Suppress("SpellCheckingInspection")

plugins { kotlin }

generateDependencies {
  upperCamelCase {
    !(it.contains("ios", ignoreCase = true)
      || it.contains("wasm32", ignoreCase = true)
      || it.contains("wasm64", ignoreCase = true)
      )
  }
  classNameTransform {
    when(it) {
      "Io" -> "IO" // Exact replacement
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
        .replace("watchosx", "WatchOS.X")
        .replace("watchosarm", "WatchOS.Arm")
        .replace("tvosx", "TvOS.X")
        .replace("tvosarm", "TvOS.Arm")
        .replace("wasm32", "wArm32")
        .replace("wasm64", "wArm64")
        .replace("bytebuddy", "ByteBuddy")
        .replace("okhttp", "OkHttp")
    }
  }
  classParentTransform {
    it
      .replace("org.jetbrains.kotlin", "kotlin")
      .replace("org.jetbrains.ktor", "ktor")
      .replace("org.jetbrains.intellij", "intellij")
      .replace("com.google.android", "google")
      .replace("org.chromium.net", "chromium")
      .replace("com.google.auto.service", "google")
      .replace("com.squareup", "square")
      .replace("org.junit.jupiter", "")
      .replace("-kt", "")
      .replace("io.", "IO.")
      .replace(".io", ".IO")
      .replace("gradle.dsl.x", "GradleDslX")
      .replace("app.cash", "CashApp")
      .removePrefix("com.linkedin")
      .removePrefix("com.afollestad")
      .removePrefix("me.liuwj")
      .removePrefix("com.")
      .removePrefix("cn.")
      .removePrefix("org.")
      .removePrefix("IO.github.")
      .removePrefix("IO.")
      .removePrefix("me")
  }
  declareDependencies(
    file("android.deps"),
    file("androidx.deps"),
    file("google.deps"),
    file("tencent.deps"),
    file("more.deps"),
  )
  declareMavenCentralDependencies(
    """
      com.rinorz [all]
      com.meowool [all]
      com.google.dagger
      com.google.guava
      com.google.code.gson
      com.google.auto [all]
      com.umeng [all]
      io.insert-koin [group]
      org.jetbrains.kotlin [all]
      org.jetbrains.kotlinx [all]
      com.squareup [all]
      org.jetbrains [all]
      org.jetbrains.compose
      org.jetbrains.anko
      org.jetbrains.dokka
      org.jetbrains.exposed
      org.jetbrains.ktor
      org.jetbrains.intellij
      org.jetbrains.spek
      org.apache.commons
      commons-io
      commons-logging
      org.apache.maven
      org.apache.logging [all]
      io.coil-kt
      io.arrow-kt
      app.cash [all]
      com.didiglobal [all]
      com.yalantis [all]
      com.tencent [all]
      com.firebase [all]
      com.jakewharton [all]
      com.airbnb [all]
      io.github.javaeden.orchid
      org.robolectric [all]
      org.junit [all]
      io.kotest
      org.spekframework.spek2
      io.strikt
      io.mockk
      org.mockito
      org.smali
      org.jsoup
      org.javassist
      org.conscrypt
      mysql
      com.afollestad [all]
      com.linkedin.dexmaker
      org.ow2 [all]
      net.bytebuddy
      me.liuwj.ktorm
      org.yaml
    """.trimIndent()
  )
  outputToProject()
}