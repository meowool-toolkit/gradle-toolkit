@file:Suppress("SpellCheckingInspection")

allprojects {
  group = "com.meowool.toolkit"
  version = "1.8-SNAPSHOT"
}

subprojects {
  if (!buildFile.exists()) return@subprojects

  afterEvaluate {
    // Don't let the fork of 'refreshVersion' spotless.
    if (projectDir.absolutePath.endsWith("dependencies/updater")) {
      tasks.findByName("spotlessApply")?.enabled = false
      tasks.findByName("spotlessKotlin")?.enabled = false
    }
  }

  tasks.findByName("dokkaHtml")?.enabled = false
  tasks.withType<Test> { useJUnit() }
}