@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl` }

publication.data {
  artifactId = "toolkit-meowool"
  displayName = "Meowool Gradle Toolkit"
  description = "Gradle Toolkit specific to the 'Meowool-Organization' project."
}

dependencies.apiOf(
  projects.android,
  projects.dependencies,
  projects.publisher,
  Libs.Gradle.Spotless,
)
