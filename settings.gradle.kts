@file:Suppress("SpellCheckingInspection")

plugins {
  id("com.meowool.toolkit.gradle-dsl-x") version "1.2"
}

buildscript {
  repositories {
    mavenCentral()
    google()
  }

  dependencies.classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.0")
}

gradleDslX {
  useMeowoolSpec()
  configureAllKotlinCompile {
    addFreeCompilerArgs("-Xopt-in=annotation.InternalGradleDslXApi")
  }
}

importProjects(
  includeDir = rootDir,
  excludeDirs = arrayOf(file("core/src/test"))
)