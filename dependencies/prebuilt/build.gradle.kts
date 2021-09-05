plugins { kotlin }

publication.data {
  artifactId = "toolkit-deps-prebuilt"
  displayName = "com.meowool.gradle.toolkit.internal.Dependency Pre-Built for Gradle Toolkit"
  description = "Pre-Built commonly used dependencies."
}

dependencies.api(projects.dependencies.mapper)