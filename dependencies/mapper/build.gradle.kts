@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `java-gradle-plugin`; kotlin("plugin.serialization") }

createGradlePlugin(implementationClass = "DependencyMapper")

@Suppress("GradleDependency")
dependencies {
  apiOf(
    project(":core"),
    Libs.KotlinX.Coroutines.Core,
  )
  implementationOf(
    Libs.KotlinX.Serialization.Json,
    Libs.Square.OkHttp3.OkHttp,
    Libs.Square.OkHttp3.Logging.Interceptor,
    Libs.Net.ByteBuddy.Byte.Buddy,
    "net.andreinc:mockneat:_",
    "com.tfowl.ktor:ktor-jsoup:_",
    "com.meowool.toolkit:sweekt:_",
    "io.ktor:ktor-client-okhttp:_",
    "io.ktor:ktor-client-logging:_",
    "io.ktor:ktor-client-serialization:_",
    "com.github.ben-manes.caffeine:caffeine:_"
  )
  testImplementation(gradleTestKit())
  testImplementation(Libs.Kotest.Runner.Junit5)
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile>() {
  kotlinOptions{
    apiVersion = "1.5"
    languageVersion = "1.5"
    useIR = true
  }
}