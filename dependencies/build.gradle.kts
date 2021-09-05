plugins { kotlin }

publication.data {
  artifactId = "toolkit-deps"
  displayName = "Dependencies for Gradle Toolkit"
  description = "Contains deps-prebuilt, deps-mapper, deps-updater."
}

dependencies.apiOf(
  projects.dependencies.mapper,
  projects.dependencies.updater,
  projects.dependencies.prebuilt,
)
