@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl`; `kotlin-kapt` }

createGradlePlugin("com.meowool.toolkit.gradle.Publisher")

publishingData {
  artifact = "gradle-publisher"
  name = "Gradle Toolkit Publisher"
}

dependencies {
  apiOf(
    Libs.Meowool.Toolkit.Sweekt,
    Libs.Mbonnin.Vespene.Lib,
    Libs.Jetbrains.Dokka.Gradle.Plugin,
  )
  implementation(Libs.Gradle.Publish.Plugin)
  compileOnly(Libs.Android.Tools.Build.Gradle version "4.2.1")
}

//publication {
//  releaseSigning = true
//  snapshotSigning = false
//  dokkaFormat = DokkaFormat.Html
//  repositories.gradlePluginPortal()
//  data {
//
//  }
//}