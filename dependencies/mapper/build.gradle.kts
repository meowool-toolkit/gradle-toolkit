@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-kapt` }

@Suppress("GradleDependency")
dependencies {
  implementationOf(
    gradleKotlinDsl(),
    Libs.Square.OkHttp3.OkHttp,
    Libs.Net.ByteBuddy.Byte.Buddy,
    "net.andreinc:mockneat:_",
    "com.tfowl.ktor:ktor-jsoup:_",
    "io.ktor:ktor-client-okhttp:_",
    "io.ktor:ktor-client-logging:_",
    "com.meowool.toolkit:sweekt:_",
  )
  kapt(Libs.Square.Moshi.Kotlin.Codegen)
  testImplementation(Libs.Kotest.Runner.Junit5)
}

tasks.withType<Test> {
  useJUnitPlatform()
}