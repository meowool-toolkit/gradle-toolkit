@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl`; `kotlin-kapt` }

repositories.gradlePluginPortal()

dependencies {
  apiOf(
    project(":core"),
    Libs.Jetbrains.Dokka.Gradle.Plugin,
    "com.vanniktech:gradle-maven-publish-plugin:_",
  )
  testImplementation(Libs.Kotlin.Test.Junit)
}