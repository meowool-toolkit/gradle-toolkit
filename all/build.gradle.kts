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

dependencies {
  api(project(":core"))
  api(project(":android"))
  api(project(":dependencies"))
  api(project(":meowool"))
  api(project(":publish"))
}