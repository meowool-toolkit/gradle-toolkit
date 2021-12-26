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

import com.android.build.api.dsl.BaseFlavor
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.DefaultConfig
import com.android.build.gradle.internal.scope.GlobalScope
import com.meowool.sweekt.castOrNull
import com.meowool.sweekt.ifNull
import org.gradle.api.Project

/**
 * Returns true if the current android extension belong to the android application project.
 *
 * @see AppExtension
 */
val BaseExtension.isApplication: Boolean get() = this is AppExtension

/**
 * Returns true if the current android extension belong to the android library project.
 *
 * @see LibraryExtension
 */
val BaseExtension.isLibrary: Boolean get() = this is LibraryExtension

/**
 * Bridge to the [DefaultConfig.applicationId].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun BaseExtension.applicationId(applicationId: String) {
  defaultConfig.applicationId = applicationId
}

/**
 * Bridge to the [DefaultConfig.minSdkVersion].
 */
fun BaseExtension.minSdk(version: String) {
  defaultConfig.minSdkVersion(version)
}

/**
 * Bridge to the [DefaultConfig.minSdkVersion].
 */
fun BaseExtension.minSdk(version: Int) {
  defaultConfig.minSdkVersion(version)
}

/**
 * Bridge to the [DefaultConfig.targetSdkVersion].
 */
fun BaseExtension.targetSdk(version: String) {
  defaultConfig.targetSdkVersion(version)
}

/**
 * Bridge to the [DefaultConfig.targetSdkVersion].
 */
fun BaseExtension.targetSdk(version: Int) {
  defaultConfig.targetSdkVersion(version)
}

/**
 * Bridge to the [DefaultConfig.versionCode].
 */
fun BaseExtension.versionCode(code: Int) {
  defaultConfig.versionCode(code)
}

/**
 * Bridge to the [DefaultConfig.versionName].
 */
fun BaseExtension.versionName(name: String) {
  defaultConfig.versionName(name)
}

/**
 * Bridge to the [DefaultConfig.versionNameSuffix].
 */
fun BaseExtension.versionNameSuffix(name: String) {
  defaultConfig.versionNameSuffix(name)
}

/**
 * Bridge to the [BaseFlavor.buildConfigField].
 */
fun BaseExtension.buildConfigField(type: String, name: String, value: String) {
  defaultConfig.buildConfigField(type, name, value)
}

internal fun BaseExtension.getGlobalProject(): Project {
  val globalScope = BaseExtension::class.java.declaredFields
    .firstOrNull { it.type == GlobalScope::class.java }
    ?.apply { isAccessible = true }?.get(this)
    .ifNull {
      BaseExtension::class.java.declaredMethods
        .firstOrNull { it.returnType == GlobalScope::class.java }
        ?.apply { isAccessible = true }?.invoke(this)
    }.castOrNull<GlobalScope>() ?: error("Unable to get the 'GlobalScope' in 'BaseExtension'!")

  val project = GlobalScope::class.java.declaredFields
    .firstOrNull { it.type == Project::class.java }
    ?.apply { isAccessible = true }?.get(globalScope)
    .ifNull {
      GlobalScope::class.java.declaredMethods
        .firstOrNull { it.returnType == Project::class.java }
        ?.apply { isAccessible = true }?.invoke(globalScope)
    }.castOrNull<Project>() ?: error("Unable to get the 'Project' in 'GlobalScope'!")

  return project
}
