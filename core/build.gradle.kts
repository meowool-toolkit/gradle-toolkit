@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl` }

gradlePlugin {
  plugins {
    create("gradleDslXCorePlugin") {
      id = "$group.gradle-dsl-x-core"
      implementationClass = "GradleDslXCore"
      displayName = findProperty("POM_NAME")!!.toString()
      description = findProperty("POM_DESCRIPTION")!!.toString()
    }
  }
}

dependencies {
  implementationOf(
    gradleKotlinDsl(),
    Libs.Kotlin.Stdlib
  )
  api(Libs.Kotlin.Gradle.Plugin)
  testImplementation(Libs.Kotlin.Test.Junit)
}