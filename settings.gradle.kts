plugins {
  id("com.meowool.toolkit.gradle-dsl-x") version "1.4"
}

buildscript {
  repositories {
    mavenCentral()
    google()
  }
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