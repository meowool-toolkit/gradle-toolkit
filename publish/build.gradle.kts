@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl`; `kotlin-kapt` }

repositories.gradlePluginPortal()

dependencies {
  api(project(":core"))
  api("com.vanniktech:gradle-maven-publish-plugin:_")
  api(Libs.Jetbrains.Dokka.Gradle.Plugin)
  testImplementation(kotlin("test-junit"))
}