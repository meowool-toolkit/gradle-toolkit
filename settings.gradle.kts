@file:Suppress("SpellCheckingInspection")

pluginManagement {
  repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}

plugins {
  id("com.meowool.toolkit.gradle") version "0.1.3-SNAPSHOT"
}

buildscript {
  dependencies.classpath(files("build/tmp/deps-mapping/1630590420237.jar"))
  configurations.all {
    // Check for updates every build
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
  }
}

gradleToolkit {
  useMeowoolSpec(publishRootProject = false)
  subprojects {
    optIn("annotation.InternalGradleToolkitApi")
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
      kotlinJvmOptions {
        useIR = true
        apiVersion = "1.5"
        languageVersion = "1.5"
        addFreeCompilerArgs("-Xskip-prerelease-check")
      }
      dependencies {
        // It doesn't make sense to add dependencies to projects that are not configurations
        if (configurations.isEmpty()) return@dependencies
        // All projects depend on the 'core'
        if (project.name != "core") {
          apiProjects(":core")
        }
        apiOf(
          Libs.Kotlin.Stdlib,
          Libs.KotlinX.Coroutines.Core,
        )
        implementation(gradleKotlinDsl())
        testImplementationOf(
          gradleTestKit(),
          Libs.Kotest.Runner.Junit5
        )
      }
      // Don't let the fork of 'refreshVersion' spotless.
      if (projectDir.absolutePath.endsWith("dependencies/updater")) {
        tasks.findByName("spotlessApply")?.enabled = false
        tasks.findByName("spotlessKotlin")?.enabled = false
      }
      tasks.withType<Test> { useJUnitPlatform() }
    }
  }
  rootProject.publishingData {
    name = "Gradle Toolkit"
    artifact = "gradle"
    group = "com.meowool.toolkit"
    version = "0.1.3-SNAPSHOT"
    description = "Raise the practicality of gradle to a new level."
    url = "https://github.com/meowool-toolkit/gradle-toolkit/"
    scmUrl = "https://github.com/meowool-toolkit/gradle-toolkit.git"
    developer {
      id = "rin"
      name = "Rin Orz"
      url = "https://github.com/RinOrz/"
    }
  }
  dependencyMapper.mapDependencies(
    "net.andreinc:mockneat",
    // TODO Remove when meowool-sweekt released.
    "com.meowool.toolkit:sweekt",
    "net.mbonnin.vespene:vespene-lib",
  )
}

importProjects(
  includeDir = rootDir,
  excludeDirs = arrayOf(file("core/src/test"))
)