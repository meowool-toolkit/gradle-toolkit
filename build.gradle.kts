@file:Suppress("SpellCheckingInspection")

subprojects {
  if (!buildFile.exists()) return@subprojects

  afterEvaluate {
    configureGradlePlugin(tags = listOf(
      "kotlin",
      "kotlin-dsl",
      "gradle-dsl",
      "gradle-utils",
      "gradle-toolkit",
      "dependency",
      "dependency-updater",
    ))

    // Don't let the fork of 'refreshVersion' spotless.
    if (projectDir.absolutePath.endsWith("dependencies/updater")) {
      tasks.findByName("spotlessApply")?.enabled = false
      tasks.findByName("spotlessKotlin")?.enabled = false
    }
  }

  tasks.findByName("dokkaHtml")?.enabled = false
  tasks.withType<Test> {
    useJUnit()
    useJUnitPlatform()
  }
}