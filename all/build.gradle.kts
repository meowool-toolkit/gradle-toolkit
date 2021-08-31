plugins { kotlin; `kotlin-dsl` }

createGradlePlugin(implementationClass = "GradleToolkit")

dependencies {
  apiProjects(
    ":core",
    ":android",
    ":dependencies",
    ":meowool",
    ":publish"
  )
  testImplementationOf(
    gradleTestKit(),
    Libs.Kotest.Runner.Junit5
  )
}