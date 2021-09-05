@file:Suppress("SpellCheckingInspection")

pluginManagement {
  repositories {
    mavenLocal()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}

plugins {
  id("com.meowool.gradle.toolkit") version "0.1.0-LOCAL-SNAPSHOT"
}

buildscript {
  dependencies.classpath(files("build/tmp/deps-mapping/1630778845508.jar"))

  repositories {
    mavenLocal()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
    gradlePluginPortal()
    google()
  }
//  dependencies.classpath("com.meowool.gradle:toolkit:0.1.0-LOCAL-SNAPSHOT")
//  dependencies.classpath(files("build/tmp/deps-mapping/1630666417944.jar"))
  configurations.all {
    // Check for updates every build
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

//apply(plugin = "com.meowool.gradle.toolkit")

gradleToolkit {
  useMeowoolSpec()
  dependencyMapper.mapDependencies(
    "net.andreinc:mockneat",
    // TODO Remove when meowool-sweekt released.
    "com.meowool.toolkit:sweekt",
    "net.mbonnin.vespene:vespene-lib",
  )
  dependencyMapperPrebuilt()
  rootProject.publication.data {
    displayName = "Gradle Toolkit"
    artifactId = "toolkit"
    groupId = "com.meowool.gradle"
    version = "0.1.0-LOCAL-SNAPSHOT"
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
  subprojects {
    optIn("com.meowool.gradle.toolkit.internal.InternalGradleToolkitApi")
    kotlinJvmOptions {
      useIR = true
      apiVersion = "1.5"
      languageVersion = "1.5"
      addFreeCompilerArgs("-Xskip-prerelease-check")
    }
    dependencies {
      // It doesn't make sense to add dependencies to projects that are not configurations
      if (project.path == ":integration-testing:android") return@dependencies
      // All projects depend on the 'core'
      if (project.name != "core") {
        apiProjects(":core")
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
    // Don't let the fork of 'refreshVersion' spotless.
    if (projectDir.absolutePath.endsWith("dependencies/updater")) {
      tasks.findByName("spotlessApply")?.enabled = false
      tasks.findByName("spotlessKotlin")?.enabled = false
    }
    tasks.withType<Test> { useJUnitPlatform() }
  }
}

importProjects(
  includeDir = rootDir,
  excludeDirs = arrayOf(file("core/src/test"))
)