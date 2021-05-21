@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl`; `kotlin-kapt` }

dependencies {
  apiOf(
    project(":core"),
    "com.vanniktech:gradle-maven-publish-plugin:_",
    Libs.Jetbrains.Dokka.Gradle.Plugin,
  )
  testImplementation(Libs.Kotlin.Test.Junit)
}