plugins { kotlin; `kotlin-dsl` }

publication.pluginClass = "$group.toolkit.GradleToolkitPlugin"

dependencies.apiProjects(
  Projects.Android,
  Projects.Meowool,
  Projects.Publisher,
  Projects.Dependency,
)