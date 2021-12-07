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
 *
 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
package com.meowool.gradle.toolkit.android.internal

import androidTest
import com.android.build.api.dsl.SigningConfig
import com.android.build.gradle.BaseExtension
import findPropertyOrEnv
import main
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import test
import java.io.File

/**
 * Here are used to load some common presets of android extension, of course you can overwrite
 * them at any time.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal fun BaseExtension.loadAndroidPresets() {
  compileSdkVersion(30)
  defaultConfig {
    targetSdkVersion(30)
    minSdkVersion(21)
    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    consumerProguardFiles("consumer-rules.pro")
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
  sourceSets {
    main.java.srcDirs("src/main/kotlin")
    test.java.srcDirs("src/test/kotlin")
    androidTest.java.srcDirs("src/androidTest/kotlin")
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}

/**
 * Here are used to load some common presets of signing config, of course you can overwrite
 * them at any time.
 */
internal fun Project.loadSigningConfigPresets(signingConfig: SigningConfig) {
  signingConfig.apply {
    findPropertyOrEnv("key.store.file")?.toString()?.let(::File)?.also { file ->
      require(file.exists()) {
        "The properties in the ${projectDir.absolutePath} project specify the `key.file`, " +
          "but the ${file.absolutePath} file does not exist."
      }
      storeFile = file
    }
    findPropertyOrEnv("key.alias")?.toString()?.also { keyAlias = it }
    findPropertyOrEnv("key.password")?.toString()?.also { keyPassword = it; storePassword = it }
    findPropertyOrEnv("key.store.password")?.toString()?.also { storePassword = it }
  }
}
