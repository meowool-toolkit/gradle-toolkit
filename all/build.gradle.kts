plugins { kotlin; `kotlin-dsl` }

gradlePlugin {
  plugins {
    create("gradleDslXPlugin") {
      id = "$group.gradle-dsl-x"
      implementationClass = "GradleDslX"
      displayName = findProperty("pom.name")!!.toString()
      description = findProperty("pom.description")!!.toString()
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