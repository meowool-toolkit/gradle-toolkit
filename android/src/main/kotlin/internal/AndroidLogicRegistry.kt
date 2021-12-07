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

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.meowool.gradle.toolkit.LogicRegistry
import com.meowool.gradle.toolkit.internal.InternalGradleToolkitApi
import com.meowool.sweekt.cast
import com.meowool.sweekt.castOrNull
import org.gradle.api.Project
import java.util.concurrent.ConcurrentHashMap

/**
 * @author 凛 (https://github.com/RinOrz)
 */
@InternalGradleToolkitApi
object AndroidLogicRegistry {
  /** The default key is used for registration and injection of android logic. */
  internal const val DefaultAndroidKey = "default android logic"

  /** The default candidate key is used for registration and injection of android logic. */
  const val DefaultCandidateAndroidKey = "default android candidate logic"

  private fun LogicRegistry.getOrCreateLogicPool(type: String): ConcurrentHashMap<Any, Any> =
    extraLogics.getOrPut("@#$%android??$type+") { ConcurrentHashMap<Any, Any>() }.cast()

  /** [TestedExtension] */
  internal inline fun LogicRegistry.androidCommonLogics(block: ConcurrentHashMap<Any, Any>.() -> Unit) {
    getOrCreateLogicPool("common").apply(block)
  }

  /** [BaseAppModuleExtension] */
  internal inline fun LogicRegistry.androidAppLogics(block: ConcurrentHashMap<Any, Any>.() -> Unit) {
    getOrCreateLogicPool("application").apply(block)
  }

  /** [LibraryExtension] */
  internal inline fun LogicRegistry.androidLibLogics(block: ConcurrentHashMap<Any, Any>.() -> Unit) {
    getOrCreateLogicPool("library").apply(block)
  }

  /** [TestedExtension] */
  internal fun LogicRegistry.getAndroidCommonLogic(key: Any): (TestedExtension.(Project) -> Unit)? =
    getOrCreateLogicPool("common")[key].castOrNull()

  /** [BaseAppModuleExtension] */
  internal fun LogicRegistry.getAndroidAppLogic(key: Any): (BaseAppModuleExtension.(Project) -> Unit)? =
    getOrCreateLogicPool("application")[key].castOrNull()

  /** [LibraryExtension] */
  internal fun LogicRegistry.getAndroidLibLogic(key: Any): (LibraryExtension.(Project) -> Unit)? =
    getOrCreateLogicPool("library")[key].castOrNull()
}
