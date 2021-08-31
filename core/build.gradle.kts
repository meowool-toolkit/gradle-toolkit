@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl` }

createGradlePlugin(implementationClass = "GradleToolkitCore")

dependencies {
  apiOf(
    gradleKotlinDsl(),
    Libs.Kotlin.Stdlib,
    Libs.Kotlin.Gradle.Plugin,
  )
  testImplementation(Libs.Kotlin.Test.Junit)
}