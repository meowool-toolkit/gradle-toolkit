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
@file:Suppress("UNCHECKED_CAST")

import LogicRegistry.Companion.notFoundKey
import LogicRegistry.Companion.requireNotKey
import annotation.InternalGradleToolkitApi
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import java.util.concurrent.ConcurrentHashMap

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
fun LogicRegistry.android(key: Any = DefaultAndroidKey, logic: TestedExtension.(project: Project) -> Unit) {
  androidLogic("common").apply {
    requireNotKey(key)
    set(key, logic)
  }
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
fun LogicRegistry.androidApp(key: Any = DefaultAndroidKey, logic: BaseAppModuleExtension.(project: Project) -> Unit) {
  androidLogic("application").apply {
    requireNotKey(key)
    set(key, logic)
  }
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
fun LogicRegistry.androidLib(key: Any = DefaultAndroidKey, logic: LibraryExtension.(project: Project) -> Unit) {
  androidLogic("library").apply {
    requireNotKey(key)
    set(key, logic)
  }
}

/**
 * Injects the shared logic of common android block registered in [LogicRegistry] into this project.
 *
 * @param key The key of the registered logic.
 * @param ignoreUnregistered Ignore injection if the logic of the specified [key] is not registered.
 *
 * @see LogicRegistry.android
 */
fun Project.injectAndroidLogic(key: Any = DefaultAndroidKey, ignoreUnregistered: Boolean = false) = android {
  val logic = logicRegistry.androidLogic("common")[key] as? TestedExtension.(Project) -> Unit
  if (ignoreUnregistered && logic == null) return@android
  logic ?: notFoundKey(key)
  logic(project)
}

/**
 * Injects the shared logic of android application block registered in [LogicRegistry] into this project.
 *
 * @param key The key of the registered logic.
 * @param ignoreUnregistered Ignore injection if the logic of the specified [key] is not registered.
 *
 * @see LogicRegistry.android
 */
fun Project.injectAndroidAppLogic(key: Any = DefaultAndroidKey, ignoreUnregistered: Boolean = false) = android {
  this as? BaseAppModuleExtension
    ?: error("This is not an android application project, please make sure to apply the `android` plugin before inject.")
  val logic = logicRegistry.androidLogic("application")[key] as? BaseAppModuleExtension.(Project) -> Unit
  if (ignoreUnregistered && logic == null) return@android
  logic ?: notFoundKey(key)
  logic(project)
}

/**
 * Injects the shared logic of android application block registered in [LogicRegistry] into this project.
 *
 * @param key The key of the registered logic.
 * @param ignoreUnregistered Ignore injection if the logic of the specified [key] is not registered.
 *
 * @see LogicRegistry.android
 */
fun Project.injectAndroidLibLogic(key: Any = DefaultAndroidKey, ignoreUnregistered: Boolean = false) = android {
  this as? LibraryExtension
    ?: error("This is not an android application project, please make sure to apply the `android-library` plugin before inject.")
  val logic = logicRegistry.androidLogic("library")[key] as? LibraryExtension.(Project) -> Unit
  if (ignoreUnregistered && logic == null) return@android
  logic ?: notFoundKey(key)
  logic(project)
}

/** The default key is used for registration and injection of android logic. */
internal const val DefaultAndroidKey = "default android logic"

@InternalGradleToolkitApi
const val DefaultInternalAndroidKey = "default android logic" // Spare


private fun LogicRegistry.androidLogic(type: String) =
  extraLogics.getOrPut("@#$%android??$type+") { ConcurrentHashMap<Any, Any>() } as ConcurrentHashMap<Any, Any>