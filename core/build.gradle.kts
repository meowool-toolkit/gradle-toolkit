@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl` }

createGradlePlugin(implementationClass = "GradleToolkitCore")

publishingData {
  artifact = "gradle-core"
  name = "Gradle Toolkit Core"
}

dependencies.api(Libs.Kotlin.Gradle.Plugin)