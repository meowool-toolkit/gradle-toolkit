@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl`; `kotlin-kapt` }

publishingData {
  artifact = "gradle-publish"
  name = "Gradle Toolkit Publish Module"
}

dependencies {
  apiOf(
    Libs.Mbonnin.Vespene.Lib,
    Libs.Jetbrains.Dokka.Gradle.Plugin,
  )
  implementation(Libs.Gradle.Publish.Plugin)
  compileOnly(Libs.Android.Tools.Build.Gradle version "4.2.1")
}