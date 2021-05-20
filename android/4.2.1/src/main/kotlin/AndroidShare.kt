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
import extension.GradleDslExtension
import extension.RootGradleDslExtension
import org.gradle.api.Project

/**
 * Share a reusable android common [configuration] block.
 *
 * NOTE: Very useful when having the same configs in multi-modules of android,
 * this can greatly improve code conciseness.
 *
 * ```
 * shareAndroid {
 *   // define common configurations.
 *   versionCode(1)
 *   versionName("1.0)
 * }
 * shareAndroid("1") {
 *   // define scope-1 configuration.
 *   versionCode(1)
 *   versionName("1.0)
 * }
 * ```
 *
 * @see GradleDslExtension.scope
 * @param scope representative the effect scope of this dependencies shared block.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun RootGradleDslExtension.shareAndroid(
  scope: String? = null,
  configuration: TestedExtension.(project: Project) -> Unit
) {
  data += AndroidConfigurationStore().apply {
    shareCommonConfiguration(scope, configuration)
  }
}

/**
 * Share a reusable android application [configuration] block.
 *
 * NOTE: Very useful when having the same configs in multi-modules of android,
 * this can greatly improve code conciseness.
 *
 * ```
 * shareAndroidApp {
 *   // define common configurations.
 *   versionCode(1)
 *   versionName("1.0)
 * }
 * shareAndroidApp("1") {
 *   // define scope-1 configuration.
 *   versionCode(1)
 *   versionName("1.0)
 * }
 * ```
 *
 * @see GradleDslExtension.scope
 * @param scope representative the effect scope of this dependencies shared block.
 */
fun RootGradleDslExtension.shareAndroidApp(
  scope: String? = null,
  configuration: BaseAppModuleExtension.(project: Project) -> Unit
) {
  data += AndroidConfigurationStore().apply {
    shareAppConfiguration(scope, configuration)
  }
}

/**
 * Share a reusable android library [configuration] block.
 *
 * NOTE: Very useful when having the same configs in multi-modules of android,
 * this can greatly improve code conciseness.
 *
 * ```
 * shareAndroidApp {
 *   // define common configurations.
 *   versionCode(1)
 *   versionName("1.0)
 * }
 * shareAndroidApp("1") {
 *   // define scope-1 configuration.
 *   versionCode(1)
 *   versionName("1.0)
 * }
 * ```
 *
 * @see GradleDslExtension.scope
 * @param scope representative the effect scope of this dependencies shared block.
 */
fun RootGradleDslExtension.shareAndroidLib(
  scope: String? = null,
  configuration: LibraryExtension.(project: Project) -> Unit
) {
  data += AndroidConfigurationStore().apply {
    shareLibConfiguration(scope, configuration)
  }
}