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
 */
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * The extension [configuration] of the Android-Application project.
 *
 * @param key Automatically inject the registered logic by the key, do nothing if the logic corresponding
 *   to the key is not registered.
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.androidApp(key: Any = DefaultAndroidKey, configuration: BaseAppModuleExtension.() -> Unit = {}) {
  if (extensions.findByName("android") !is BaseAppModuleExtension) apply(plugin = "android")
  init(key)
  android {
    this as BaseAppModuleExtension
    configuration()
  }
  // Use registered application-logic
  injectAndroidAppLogic(key, ignoreUnregistered = true)
  injectAndroidAppLogic(DefaultInternalAndroidKey, ignoreUnregistered = true)
}

/**
 * The extension [configuration] of the Android-Library project.
 * If the `kotlin-android` plugin is not enabled for this android app project, it will be automatically enabled.
 *
 * @param key Automatically inject the registered logic by the key, do nothing if the logic corresponding
 *   to the key is not registered.
 */
fun Project.androidKotlinApp(key: Any = DefaultAndroidKey, configuration: BaseAppModuleExtension.() -> Unit = {}) {
  if (plugins.hasPlugin("kotlin-android").not()) apply(plugin = "kotlin-android")
  androidApp(key, configuration)
}

/**
 * The extension [configuration] of the Android-Library project.
 *
 * @param key Automatically inject the registered logic by the key, do nothing if the logic corresponding
 *   to the key is not registered.
 */
fun Project.androidLib(key: Any = DefaultAndroidKey, configuration: LibraryExtension.() -> Unit = {}) {
  if (extensions.findByName("android") !is LibraryExtension) apply(plugin = "android-library")
  init(key)
  android {
    this as LibraryExtension
    configuration()
  }
  // Use registered library-logic
  injectAndroidLibLogic(key, ignoreUnregistered = true)
  injectAndroidLibLogic(DefaultInternalAndroidKey, ignoreUnregistered = true)
}

/**
 * The extension [configuration] of the Android-Library project.
 * If the `kotlin-android` plugin is not enabled for this android library project, it will be automatically enabled.
 *
 * @param key Automatically inject the registered logic by the key, do nothing if the logic corresponding
 *   to the key is not registered.
 */
fun Project.androidKotlinLib(key: Any = DefaultAndroidKey, configuration: LibraryExtension.() -> Unit = {}) {
  if (plugins.hasPlugin("kotlin-android").not()) apply(plugin = "kotlin-android")
  androidLib(key, configuration)
}


internal fun Project.android(configuration: TestedExtension.() -> Unit) {
  requireApplyPlugin()
  extensions.getByName("android").apply {
    this as TestedExtension
    configuration()
  }
}

private fun Project.init(key: Any) {
  android {
    loadAndroidPresets()
  }
  // Use registered common-logic
  injectAndroidLogic(key, ignoreUnregistered = true)
  injectAndroidLogic(DefaultInternalAndroidKey, ignoreUnregistered = true)
}