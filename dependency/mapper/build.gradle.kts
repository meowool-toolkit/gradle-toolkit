plugins { kotlin; `kotlin-dsl`; kotlin("plugin.serialization") }

publication {
  data {
    artifactId = "toolkit-dependency-mapper"
    displayName = "com.meowool.gradle.toolkit.internal.Dependency Mapper for Gradle Toolkit"
    description = "Map all dependencies to classes and fields for easy calling in gradle scripts."
  }
  pluginClass = "$group.toolkit.DependencyMapperPlugin"
}

dependencies.implementationOf(
  Libs.Jsoup,
  Libs.Caffeine,
  Libs.Ktor.Client.OkHttp,
  Libs.Ktor.Client.Logging,
  Libs.Ktor.Client.Serialization,
  Libs.KotlinX.Serialization.Json,
  Libs.Square.OkHttp3.Logging.Interceptor,
  Libs.ByteBuddy.Byte.Buddy,
  Libs.Andreinc.Mockneat,
)