@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl`; `kotlin-kapt` }

dependencies {
  apiOf(
    project(":core"),
    Libs.Jetbrains.Dokka.Gradle.Plugin,
  )
  compileOnly(Libs.Android.Tools.Build.Gradle version "4.2.1")
  testImplementation(Libs.Kotlin.Test.Junit)
}