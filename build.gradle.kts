@file:Suppress("SpellCheckingInspection")

// Root data, not publish (all sub-projects extends from here)
publication.data {
  displayName = "Gradle Toolkit"
  artifactId = "toolkit"
  groupId = "com.meowool.gradle"
  version = "0.2.0-SNAPSHOT"
  description = "Raise the practicality of gradle to a new level."
  url = "https://github.com/meowool-toolkit/gradle-toolkit/"
  developer {
    id = "rin"
    name = "Rin Orz"
    url = "https://github.com/RinOrz/"
  }
  tags(
    "kotlin",
    "kotlin-dsl",
    "gradle-dsl",
    "gradle-utils",
    "gradle-toolkit",
    "dependency",
    "dependency-updater",
  )
}

subdependencies {
  // All projects depend on the ':core'
  if (project.path != Projects.Core) {
    apiProject(Projects.Core)
  }
  apiOf(
    gradleKotlinDsl(),
    Libs.Kotlin.Stdlib,
    Libs.KotlinX.Coroutines.Core,
    Libs.Meowool.Toolkit.Sweekt,
  )
  testImplementationOf(
    gradleTestKit(),
    Libs.Kotest.Runner.Junit5
  )
}

subprojects {
  tasks.withType<Test> { useJUnitPlatform() }
  // Don't let the fork of 'refreshVersion' spotless.
  if (projectDir.absolutePath.endsWith("dependencies/updater")) {
    tasks.findByName("spotlessApply")?.enabled = false
    tasks.findByName("spotlessKotlin")?.enabled = false
  }
  kotlinJvmOptions {
    @Suppress("DEPRECATION")
    useIR = true
    apiVersion = "1.5"
    languageVersion = "1.5"
    addFreeCompilerArgs("-Xskip-prerelease-check")
  }
  optIn("com.meowool.gradle.toolkit.internal.InternalGradleToolkitApi")
}