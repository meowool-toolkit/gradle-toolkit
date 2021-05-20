plugins { kotlin; `kotlin-dsl` }

gradlePlugin {
  plugins {
    create("gradleDslXPlugin") {
      id = "$group.gradle-dsl-x"
      implementationClass = "GradleDslX"
      displayName = findProperty("POM_NAME")!!.toString()
      description = findProperty("POM_DESCRIPTION")!!.toString()
    }
  }
}

dependencies.apiProjects(
  ":core",
  ":android",
  ":dependencies",
  ":meowool",
  ":publish"
)