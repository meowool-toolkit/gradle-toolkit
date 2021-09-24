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

buildscript { repositories.mavenCentral() }

// Root data, not publish (all sub-projects extends from here)
publication.data {
  displayName = "Gradle Toolkit"
  groupId = "com.meowool.gradle"
  version = "0.2.2-LOCAL-SNAPSHOT"
  // Used to publish non-local versions of artifacts in CI environment
  versionInCI = "0.2.2-SNAPSHOT"
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

val internalMarkers = arrayOf(
  "com.meowool.gradle.toolkit.internal.InternalGradleToolkitApi",
  "de.fayard.refreshVersions.core.internal.InternalRefreshVersionsApi"
)

metalava {
  hiddenAnnotations(*internalMarkers)
  hiddenPackages(
    "com.meowool.gradle.toolkit.internal",
    "com.meowool.gradle.toolkit.android.internal",
    "com.meowool.gradle.toolkit.publisher.internal",
    "de.fayard.refreshVersions.internal",
    "de.fayard.refreshVersions.core.internal",
  )
}

subprojects {
  optIn(*internalMarkers)
  kotlinJvmOptions {
    @Suppress("DEPRECATION")
    useIR = true
  }
  tasks.withType<Test> { useJUnitPlatform() }
}

subdependencies {
  if (project.path.startsWith(Projects.Integration.Testing)) return@subdependencies
  // All projects depend on the ':core'
  if (project.path != Projects.Core) {
    apiProject(Projects.Core)
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
