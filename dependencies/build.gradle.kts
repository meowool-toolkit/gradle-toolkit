plugins { kotlin }

publishingData {
  artifact = "gradle-deps"
  name = "Dependencies for Gradle Toolkit"
  description = "Contains deps-prebuilt, deps-mapper, deps-updater."
}

dependencies.apiProjects(
  ":dependencies:mapper",
  ":dependencies:prebuilt",
  ":dependencies:updater"
)
