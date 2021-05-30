plugins { kotlin; `kotlin-dsl` }

createGradlePlugin(implementationClass = "GradleDslX")

dependencies.apiProjects(
  ":core",
  ":android",
  ":dependencies",
  ":meowool",
  ":publish"
)