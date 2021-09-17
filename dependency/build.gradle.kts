plugins { kotlin }

publication.data {
  artifactId = "toolkit-dependency"
  displayName = "Dependencies for Gradle Toolkit"
  description = "Contains deps-prebuilt, deps-mapper, deps-updater."
}

dependencies.apiProjects(
  Projects.Dependency.Mapper,
  Projects.Dependency.Updater,
  Projects.Dependency.Prebuilt,
)
