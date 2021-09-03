plugins { kotlin; `kotlin-dsl` }

createGradlePlugin(implementationClass = "GradleToolkit")

dependencies.apiProjects(
  ":android",
  ":dependencies",
  ":meowool",
  ":publisher"
)