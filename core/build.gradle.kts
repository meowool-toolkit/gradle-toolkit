@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl` }

createGradlePlugin(implementationClass = "GradleDslXCore")

dependencies {
  apiOf(
    gradleKotlinDsl(),
    Libs.Kotlin.Stdlib,
    Libs.Kotlin.Gradle.Plugin,
  )
  implementation(Libs.Kotlin.Stdlib)
  testImplementation(Libs.Kotlin.Test.Junit)
}