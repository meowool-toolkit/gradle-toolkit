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
@file:Suppress("UNCHECKED_CAST")

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.meowool.gradle.toolkit.LogicRegistry
import com.meowool.gradle.toolkit.LogicRegistry.Companion.logicRegistry
import com.meowool.gradle.toolkit.LogicRegistry.Companion.notFoundKey
import com.meowool.gradle.toolkit.LogicRegistry.Companion.requireNotKey
import com.meowool.gradle.toolkit.android.internal.AndroidLogicRegistry.DefaultAndroidKey
import com.meowool.gradle.toolkit.android.internal.AndroidLogicRegistry.androidAppLogics
import com.meowool.gradle.toolkit.android.internal.AndroidLogicRegistry.androidCommonLogics
import com.meowool.gradle.toolkit.android.internal.AndroidLogicRegistry.androidLibLogics
import com.meowool.gradle.toolkit.android.internal.AndroidLogicRegistry.getAndroidAppLogic
import com.meowool.gradle.toolkit.android.internal.AndroidLogicRegistry.getAndroidCommonLogic
import com.meowool.gradle.toolkit.android.internal.AndroidLogicRegistry.getAndroidLibLogic
import com.meowool.gradle.toolkit.android.internal.android
import com.meowool.gradle.toolkit.android.internal.requireAndroidAppPlugin
import com.meowool.gradle.toolkit.android.internal.requireAndroidLibPlugin
import org.gradle.api.Project

/**
 * Registers the common android block [logic] belonging to the specified [key].
 *
 * And then you can call [Project.androidApp] or [Project.androidLib] or [injectProjectLogic] in the any required
 * project to inject [logic].
 *
 * ```
 * registerLogic {
 *   android {
 *     // Define common android logic
 *     versionCode(1)
 *     versionName("1.0")
 *   }
 *   android(1) {
 *     // Define logic of android that can only be injected by key
 *     versionCode(1)
 *     versionName("1.0")
 *   }
 * }
 * ```
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun LogicRegistry.android(
  key: Any = DefaultAndroidKey,
  logic: TestedExtension.(project: Project) -> Unit,
) = androidCommonLogics {
  requireNotKey(key)
  set(key, logic)
}

/**
 * Registers the android application block [logic] belonging to the specified [key].
 *
 * And then you can call [Project.androidApp] or [injectAndroidAppLogic] in the any required project to inject [logic].
 *
 * ```
 * registerLogic {
 *   androidApp {
 *     // Define common android application logic
 *     ...
 *   }
 *   androidApp(1) {
 *     // Define logic of android application that can only be injected by key
 *     ...
 *   }
 * }
 * ```
 */
fun LogicRegistry.androidApp(
  key: Any = DefaultAndroidKey,
  logic: BaseAppModuleExtension.(project: Project) -> Unit,
) = androidAppLogics {
  requireNotKey(key)
  set(key, logic)
}

/**
 * Registers the android library block [logic] belonging to the specified [key].
 *
 * And then you can call [Project.androidLib] or [injectAndroidLibLogic] in the any required project to inject [logic].
 *
 * ```
 * registerLogic {
 *   androidLib {
 *     // Define common android application logic
 *     ...
 *   }
 *   androidLib(1) {
 *     // Define logic of android application that can only be injected by key
 *     ...
 *   }
 * }
 * ```
 */
fun LogicRegistry.androidLib(
  key: Any = DefaultAndroidKey,
  logic: LibraryExtension.(project: Project) -> Unit,
) = androidLibLogics {
  requireNotKey(key)
  set(key, logic)
}

/**
 * Injects the shared logic of common android block registered in [LogicRegistry] into this project.
 *
 * @param key The key of the registered logic.
 * @param ignoreUnregistered Ignore injection if the logic of the specified [key] is not registered.
 *
 * @see LogicRegistry.android
 */
fun Project.injectAndroidLogic(key: Any = DefaultAndroidKey, ignoreUnregistered: Boolean = false) {
  android<TestedExtension> {
    val logic = logicRegistry.getAndroidCommonLogic(key)
    if (ignoreUnregistered && logic == null) return@android
    logic ?: notFoundKey(key)
    logic(project)
  }
}

/**
 * Injects the shared logic of android application block registered in [LogicRegistry] into this project.
 *
 * @param key The key of the registered logic.
 * @param ignoreUnregistered Ignore injection if the logic of the specified [key] is not registered.
 *
 * @see LogicRegistry.android
 */
fun Project.injectAndroidAppLogic(key: Any = DefaultAndroidKey, ignoreUnregistered: Boolean = false) {
  requireAndroidAppPlugin()
  android<BaseAppModuleExtension> {
    val logic = logicRegistry.getAndroidAppLogic(key)
    if (ignoreUnregistered && logic == null) return@android
    logic ?: notFoundKey(key)
    logic(project)
  }
}

/**
 * Injects the shared logic of android application block registered in [LogicRegistry] into this project.
 *
 * @param key The key of the registered logic.
 * @param ignoreUnregistered Ignore injection if the logic of the specified [key] is not registered.
 *
 * @see LogicRegistry.android
 */
fun Project.injectAndroidLibLogic(key: Any = DefaultAndroidKey, ignoreUnregistered: Boolean = false) {
  requireAndroidLibPlugin()
  android<LibraryExtension> {
    val logic = logicRegistry.getAndroidLibLogic(key)
    if (ignoreUnregistered && logic == null) return@android
    logic ?: notFoundKey(key)
    logic(project)
  }
}
