plugins { kotlin }

publication.data {
  artifactId = "toolkit-dependency-prebuilt"
  displayName = "com.meowool.gradle.toolkit.internal.Dependency Pre-Built for Gradle Toolkit"
  description = "Pre-Built commonly used dependencies."
}

dependencies.apiProject(Projects.Dependency.Mapper)