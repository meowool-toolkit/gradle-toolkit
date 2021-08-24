@file:Suppress("SpellCheckingInspection")

subprojects {
  if (!buildFile.exists()) return@subprojects

  afterEvaluate {
    configureGradlePlugin(
      tags = listOf("gradle-dsl", "gradle-utils", "gradle-toolkit", "kotlin", "kotlin-dsl", "ktx")
    )

    // Don't let the fork of 'refreshVersion' spotless.
    if (projectDir.absolutePath.endsWith("dependencies/updater")) {
      tasks.findByName("spotlessApply")?.enabled = false
      tasks.findByName("spotlessKotlin")?.enabled = false
    }
  }

  tasks.findByName("dokkaHtml")?.enabled = false
  tasks.withType<Test> { useJUnit() }
}