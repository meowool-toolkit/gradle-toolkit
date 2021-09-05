@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl` }

publication {
  data {
    artifactId = "toolkit-publisher"
    displayName = "Gradle Toolkit Publisher"
    description = "Pre-Built commonly used dependencies."
  }
  pluginClass = "$group.toolkit.publisher.PublisherPlugin"
}

dependencies {
  apiOf(
    Libs.Meowool.Toolkit.Sweekt,
    Libs.Mbonnin.Vespene.Lib,
    Libs.Jetbrains.Dokka.Gradle.Plugin,
  )
  implementation(Libs.Gradle.Publish.Plugin)
  compileOnly(Libs.Android.Tools.Build.Gradle version "4.2.1")
}