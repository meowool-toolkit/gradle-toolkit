plugins { kotlin }

publishingData {
  artifact = "gradle-deps-prebuilt"
  name = "Dependency Pre-Built for Gradle Toolkit"
  description = "Pre-Built commonly used dependencies."
}

dependencies.apiProjects(":dependencies:mapper")