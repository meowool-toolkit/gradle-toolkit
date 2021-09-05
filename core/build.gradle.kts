@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl` }

publication {
  data {
    artifactId = "toolkit-core"
    displayName = "Gradle Toolkit Core"
  }
  pluginClass = "$group.toolkit.GradleToolkitCorePlugin"
}

dependencies.api(Libs.Kotlin.Gradle.Plugin)