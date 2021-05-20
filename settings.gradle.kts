@file:Suppress("SpellCheckingInspection")

pluginManagement {
  repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    gradlePluginPortal()
    mavenCentral()
  }
}

buildscript {
  repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    gradlePluginPortal()
    google()
  }

  arrayOf(
//    "com.android.tools.build:gradle:4.2.1",
    "org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.0",
    "com.meowool.toolkit:gradle-dsl-x:1.0-SNAPSHOT",
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

importProjects(rootDir, file("core/src/test"))