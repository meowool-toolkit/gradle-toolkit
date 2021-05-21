@file:Suppress("SpellCheckingInspection")

buildscript {
  repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
    gradlePluginPortal()
    google()
  }

  arrayOf(
    "org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.0",
    "com.meowool.toolkit:gradle-dsl-x:1.1-SNAPSHOT",
  ).forEach { dependencies.classpath(it) }

  // Check for updates every build
  configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
  }
}

apply<GradleDslX>()

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