/*
 * Copyright (c) 2021. The Meowool Organization Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
@file:Suppress("SpellCheckingInspection")

pluginManagement {
  repositories {
    maven(file(".repo"))
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins {
  // In the Github Action environment, we use the non-local version for testing, see:
  //   https://github.com/meowool-toolkit/gradle-toolkit/blob/main/.github/workflows/deployment.yml
  id("com.meowool.gradle.toolkit") version when (System.getenv().containsKey("use.ci.version")) {
    true -> "0.2.2-SNAPSHOT"
    false -> "0.2.2-LOCAL-SNAPSHOT"
  }
}

buildscript {
  configurations.all {
    // Check for updates every build
    resolutionStrategy {
      force("com.android.tools.build:gradle:4.2.2")
      cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
  }
}

dependencyMapper {
  libraries {
    map(
      "net.andreinc:mockneat",
      // TODO Remove when meowool-sweekt released.
      "com.meowool.toolkit:sweekt",
    )
    map(
      "net.mbonnin.vespene:vespene-lib" to "Vespene",
      "be.vbgn.gradle:ci-detect-plugin" to "Gradle.CiDetectPlugin",
    )
  }
}

gradleToolkitWithMeowoolSpec(spec = {
  spotless {
    kotlinGradle {
      // Fake build scripts exist in this directory, they don’t need to be spotless
      targetExclude("core/src/test/resources/**")
    }
  }
})

importProjects(
  includeDir = rootDir,
  excludeDirs = arrayOf(file("core/src/test"))
)