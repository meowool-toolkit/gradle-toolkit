plugins { kotlin; `kotlin-dsl`; kotlin("plugin.serialization") }

createGradlePlugin(implementationClass = "DependencyMapper")

publishingData {
  artifact = "gradle-deps-mapper"
  name = "Dependency Mapper for Gradle Toolkit"
  description = "Map all dependencies to classes and fields for easy calling in gradle scripts."
}

dependencies.implementationOf(
  Libs.Ktor.Jsoup,
  Libs.Ktor.Client.OkHttp,
  Libs.Ktor.Client.Logging,
  Libs.Ktor.Client.Serialization,
  Libs.KotlinX.Serialization.Json,
  Libs.ByteBuddy.Byte.Buddy,
  Libs.Andreinc.Mockneat,
  Libs.Caffeine,
)