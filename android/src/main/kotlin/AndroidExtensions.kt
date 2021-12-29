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
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.meowool.gradle.toolkit.android.internal.AndroidLogicRegistry.DefaultAndroidKey
import com.meowool.gradle.toolkit.android.internal.AndroidLogicRegistry.DefaultCandidateAndroidKey
import com.meowool.gradle.toolkit.android.internal.android
import com.meowool.gradle.toolkit.android.internal.applyKotlinAndroidIfNotExists
import com.meowool.gradle.toolkit.android.internal.hasAndroidPlugin
import com.meowool.gradle.toolkit.android.internal.loadAndroidPresets
import com.meowool.gradle.toolkit.android.internal.requireAndroidAppPlugin
import com.meowool.gradle.toolkit.android.internal.requireAndroidLibPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * The extension [configuration] of the Android-Application project.
 *
 * @param key Automatically inject the registered logic by the key, do nothing if the logic corresponding
 *   to the key is not registered.
 *
 * @author 凛 (RinOrz)
 */
fun Project.androidApp(key: Any = DefaultAndroidKey, configuration: BaseAppModuleExtension.() -> Unit = {}) {
  when {
    hasAndroidPlugin().not() -> apply(plugin = "android")
    else -> requireAndroidAppPlugin()
  }
  // Use registered common-logic
  injectAndroidLogic(key, ignoreUnregistered = true)
  injectAndroidLogic(DefaultCandidateAndroidKey, ignoreUnregistered = true)
  // Use registered application-logic
  injectAndroidAppLogic(key, ignoreUnregistered = true)
  injectAndroidAppLogic(DefaultCandidateAndroidKey, ignoreUnregistered = true)

  android<BaseAppModuleExtension> {
    loadAndroidPresets()
    configuration()
  }
}

/**
 * The extension [configuration] of the Android-Application project.
 * If the `kotlin-android` plugin is not enabled for this android app project, it will be automatically enabled.
 *
 * @param key Automatically inject the registered logic by the key, do nothing if the logic corresponding
 *   to the key is not registered.
 *
 * @see androidApp
 */
fun Project.androidKotlinApp(key: Any = DefaultAndroidKey, configuration: BaseAppModuleExtension.() -> Unit = {}) {
  androidApp(key, configuration)
  applyKotlinAndroidIfNotExists()
}

/**
 * The extension [configuration] of the Android-Library project.
 *
 * @param key Automatically inject the registered logic by the key, do nothing if the logic corresponding
 *   to the key is not registered.
 */
fun Project.androidLib(key: Any = DefaultAndroidKey, configuration: LibraryExtension.() -> Unit = {}) {
  when {
    hasAndroidPlugin().not() -> apply(plugin = "android-library")
    else -> requireAndroidLibPlugin()
  }
  // Use registered common-logic
  injectAndroidLogic(key, ignoreUnregistered = true)
  injectAndroidLogic(DefaultCandidateAndroidKey, ignoreUnregistered = true)
  // Use registered library-logic
  injectAndroidLibLogic(key, ignoreUnregistered = true)
  injectAndroidLibLogic(DefaultCandidateAndroidKey, ignoreUnregistered = true)

  android<LibraryExtension> {
    loadAndroidPresets()
    configuration()
  }
}

/**
 * The extension [configuration] of the Android-Library project.
 * If the `kotlin-android` plugin is not enabled for this android library project, it will be automatically enabled.
 *
 * @param key Automatically inject the registered logic by the key, do nothing if the logic corresponding
 *   to the key is not registered.
 */
fun Project.androidKotlinLib(key: Any = DefaultAndroidKey, configuration: LibraryExtension.() -> Unit = {}) {
  androidLib(key, configuration)
  applyKotlinAndroidIfNotExists()
}
