/*
 * Copyright (c) $\YEAR. The Meowool Organization Open Source Project
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
@file:Suppress("NOTHING_TO_INLINE")

import com.android.build.api.dsl.BaseFlavor
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.DefaultConfig

/**
 * Bridge to the [DefaultConfig.applicationId].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
inline fun BaseExtension.applicationId(applicationId: String) {
  defaultConfig.applicationId = applicationId
}

/**
 * Bridge to the [DefaultConfig.minSdkVersion].
 */
inline fun BaseExtension.minSdk(version: String) {
  defaultConfig.minSdkVersion(version)
}

/**
 * Bridge to the [DefaultConfig.minSdkVersion].
 */
inline fun BaseExtension.minSdk(version: Int) {
  defaultConfig.minSdkVersion(version)
}

/**
 * Bridge to the [DefaultConfig.targetSdkVersion].
 */
inline fun BaseExtension.targetSdk(version: String) {
  defaultConfig.targetSdkVersion(version)
}

/**
 * Bridge to the [DefaultConfig.targetSdkVersion].
 */
inline fun BaseExtension.targetSdk(version: Int) {
  defaultConfig.targetSdkVersion(version)
}

/**
 * Bridge to the [DefaultConfig.versionCode].
 */
inline fun BaseExtension.versionCode(code: Int) {
  defaultConfig.versionCode(code)
}

/**
 * Bridge to the [DefaultConfig.versionName].
 */
inline fun BaseExtension.versionName(name: String) {
  defaultConfig.versionName(name)
}

/**
 * Bridge to the [DefaultConfig.versionNameSuffix].
 */
inline fun BaseExtension.versionNameSuffix(name: String) {
  defaultConfig.versionNameSuffix(name)
}

/**
 * Bridge to the [BaseFlavor.buildConfigField].
 */
inline fun BaseExtension.buildConfigField(type: String, name: String, value: String) {
  defaultConfig.buildConfigField(type, name, value)
}
