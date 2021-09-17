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
  id("com.meowool.gradle.toolkit") version "0.2.0-SNAPSHOT"
}

buildscript {
  configurations.all {
    // Check for updates every build
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
  }
}

dependencyMapper {
  libraries {
    map(
      "net.andreinc:mockneat",
      // TODO Remove when meowool-sweekt released.
      "com.meowool.toolkit:sweekt",
    )
    map("net.mbonnin.vespene:vespene-lib" to "Vespene")
  }
}

gradleToolkitWithMeowoolSpec()

importProjects(
  includeDir = rootDir,
  excludeDirs = arrayOf(file("core/src/test"))
)