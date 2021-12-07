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
@file:Suppress("NOTHING_TO_INLINE")

package com.meowool.gradle.toolkit.android.internal

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

internal inline fun Project.hasAndroidPlugin() = extensions.findByName("android") != null

internal inline fun Project.applyKotlinAndroidIfNotExists() {
  if (plugins.hasPlugin("kotlin-android").not()) apply(plugin = "kotlin-android")
}

internal fun Project.requireAndroidPlugin() = require(hasAndroidPlugin()) {
  "Android extension is not found, please apply `android` or `android-library` plugin first."
}

internal fun Project.requireAndroidAppPlugin() = require(extensions.findByName("android") is BaseAppModuleExtension) {
  "This is not an android application project, please make sure the applied plugin is `android` plugin: id(\"com.android.application\")"
}

internal fun Project.requireAndroidLibPlugin() = require(extensions.findByName("android") is LibraryExtension) {
  "This is not an android library project, please make sure the applied plugin is `android-library` plugin: id(\"com.android.library\")"
}

internal fun <T : TestedExtension> Project.android(configuration: T.() -> Unit) {
  requireAndroidPlugin()
  extensions.getByName("android").apply {
    @Suppress("UNCHECKED_CAST")
    this as T
    configuration()
  }
}
