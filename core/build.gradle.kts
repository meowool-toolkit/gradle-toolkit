@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl` }

gradlePlugin {
  plugins {
    create("gradleDslXCorePlugin") {
      id = "$group.gradle-dsl-x-core"
      implementationClass = "GradleDslXCore"
      displayName = findProperty("pom.name")!!.toString()
      description = findProperty("pom.description")!!.toString()
    }
  }
}

dependencies {
  apiOf(
    gradleKotlinDsl(),
    Libs.Kotlin.Gradle.Plugin,
  )
  implementation(Libs.Kotlin.Stdlib)
  testImplementation(Libs.Kotlin.Test.Junit)
}