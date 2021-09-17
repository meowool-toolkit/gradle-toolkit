@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl` }

publication.data {
  artifactId = "toolkit-meowool"
  displayName = "Meowool Gradle Toolkit"
  description = "Gradle Toolkit specific to the 'Meowool-Organization' project."
}

dependencies{
  apiProjects(
    Projects.Android,
    Projects.Dependency,
    Projects.Publisher,
  )
  api(Libs.Gradle.Spotless)
}
