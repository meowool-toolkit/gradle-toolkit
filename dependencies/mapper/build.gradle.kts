plugins { kotlin; `kotlin-dsl`; kotlin("plugin.serialization") }

publication {
  data {
    artifactId = "toolkit-deps-mapper"
    displayName = "com.meowool.gradle.toolkit.internal.Dependency Mapper for Gradle Toolkit"
    description = "Map all dependencies to classes and fields for easy calling in gradle scripts."
  }
  pluginClass = "$group.toolkit.DependencyMapperPlugin"
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