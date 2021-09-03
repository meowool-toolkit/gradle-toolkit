@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl` }

publishingData {
  artifact = "gradle-meowool"
  name = "Meowool Gradle Toolkit"
  description = "Gradle Toolkit specific to the 'Meowool-Organization' project."
}

dependencies {
  api(Libs.Gradle.Spotless)
  apiProjects(":android", ":dependencies", ":publisher")
}