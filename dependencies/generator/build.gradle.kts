@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-kapt` }

@Suppress("GradleDependency")
dependencies {
  implementationOf(
    gradleKotlinDsl(),
    Libs.Square.Moshi,
    Libs.Square.Kotlinpoet,
    Libs.Square.OkHttp3.OkHttp,
  )
  kapt(Libs.Square.Moshi.Kotlin.Codegen)
  testImplementation(Libs.Kotlin.Test.Junit)
}