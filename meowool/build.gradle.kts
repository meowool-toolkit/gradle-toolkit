@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl` }

dependencies {
  apiProjects(":core", ":android", ":dependencies", ":publish")
  api("com.diffplug.spotless:spotless-plugin-gradle:_")
  testImplementation(Libs.Kotlin.Test.Junit)
}