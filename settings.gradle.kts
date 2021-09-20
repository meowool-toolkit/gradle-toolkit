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
    mavenLocal()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}

plugins {
  id("com.meowool.gradle.toolkit") version "0.2.2-LOCAL-SNAPSHOT"
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
    map(
      "net.mbonnin.vespene:vespene-lib" to "Vespene",
      "me.tylerbwong.gradle:metalava-gradle" to "Gradle.Metalava"
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
