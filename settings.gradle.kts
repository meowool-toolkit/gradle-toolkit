@file:Suppress("SpellCheckingInspection")

pluginManagement {
  repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
    google()
    gradlePluginPortal()
  }
}

plugins {
  id("com.meowool.toolkit.gradle-dsl-x") version "1.9-SNAPSHOT"
}

buildscript {
//  dependencies.classpath("com.meowool.toolkit:gradle-dsl-x-dependencies-builtin:2.1")

  configurations.all {
    // Check for updates every build
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
  }
}

rootGradleDslX {
  useMeowoolSpec(publishRootProject = false)
  configureAllKotlinCompile {
    addFreeCompilerArgs("-Xopt-in=annotation.InternalGradleToolkitApi")
  }
}

importProjects(
  includeDir = rootDir,
  excludeDirs = arrayOf(file("core/src/test"))
)