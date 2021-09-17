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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `java-test-fixtures`
  `kotlin-dsl`
  idea
}

publication.data {
  artifactId = "toolkit-dependency-updater"
  displayName = "Dependency Updater for Gradle Toolkit"
  description = "Let dependencies have version check for updates."
}

dependencies {
  implementationOf(
    Libs.KotlinX.Coroutines.Core,
    Libs.Google.Cloud.Storage,
    Libs.Square.Moshi.Kotlin,
    Libs.Square.OkHttp3.OkHttp,
    Libs.Square.OkHttp3.Logging.Interceptor,
  )
  implementation(Libs.Square.Retrofit2.Retrofit) {
    because("It has ready to use HttpException class")
  }

  testImplementationOf(
    Libs.Junit.Jupiter,
    Libs.Kotlin.Test.Junit5,
    Libs.Kotlin.Test.Annotations.Common,
    Libs.Square.OkHttp3.Logging.Interceptor,
    platform(notation = "org.junit:junit-bom:_"),
  )

  testFixturesApi(Libs.Square.OkHttp3.OkHttp)
  testFixturesApi(Libs.Square.OkHttp3.Logging.Interceptor)
  testFixturesApi(Libs.KotlinX.Coroutines.Core)

  constraints {
    implementation(withoutVersion(Libs.Google.Guava)) {
      version {
        strictly("30.1.1-jre")
        // Without that version constraint forcing a known "jre" variant,
        // GCS makes an "android" variant being selected for the buildscript classpath,
        // which creates a conflict with the Android Gradle Plugin and possibly other plugins,
        // that would manifest itself at runtime like the following:
        // Failed to notify project evaluation listener.
        //   > 'java.util.stream.Collector com.google.common.collect.ImmutableList.toImmutableList()'
        // (The Android Gradle Plugin is not an Android app or library, so it relies on the "jre" variant.)
      }
    }
  }
}

kotlin {
  target.compilations.let {
    it.getByName("testFixtures").associateWith(it.getByName("main"))
  }
}

(components["java"] as AdhocComponentWithVariants).let { javaComponent ->
  javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
  javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }
}

val genResourcesDir = buildDir.resolve("generated/refreshVersions/resources")

sourceSets.main {
  resources.srcDir(genResourcesDir.path)
}

idea {
  module.generatedSourceDirs.add(genResourcesDir)
}

val copyVersionFile by tasks.registering {
  val versionFile = file("version.txt")
  val versionFileCopy = genResourcesDir.resolve("version.txt")
  inputs.file(versionFile)
  outputs.file(versionFileCopy)
  doFirst { versionFile.copyTo(versionFileCopy, overwrite = true) }
}

tasks.withType<KotlinCompile> {
  dependsOn(copyVersionFile)
  kotlinOptions {
    jvmTarget = "1.8"
    freeCompilerArgs = freeCompilerArgs + listOf(
      "-Xinline-classes",
      "-Xmulti-platform", // Allow using expect and actual keywords.
      "-Xopt-in=kotlin.RequiresOptIn",
      "-Xopt-in=de.fayard.refreshVersions.core.internal.InternalRefreshVersionsApi",
    )
  }
}
