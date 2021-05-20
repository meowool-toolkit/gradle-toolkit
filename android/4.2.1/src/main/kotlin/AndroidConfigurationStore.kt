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

/**
 * Store of shared configuration for android application and library.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal class AndroidConfigurationStore {
  private val commonConfigurations = hashMapOf<String, TestedExtension.(Project) -> Unit>()
  private val libConfigurations = hashMapOf<String, LibraryExtension.(Project) -> Unit>()
  private val appConfigurations = hashMapOf<String, BaseAppModuleExtension.(Project) -> Unit>()

  fun shareAppConfiguration(
    scope: String?,
    configuration: BaseAppModuleExtension.(Project) -> Unit
  ) {
    val key = scope ?: MainScope
    appConfigurations[key] = configuration
  }

  fun shareLibConfiguration(
    scope: String?,
    configuration: LibraryExtension.(Project) -> Unit
  ) {
    val key = scope ?: MainScope
    libConfigurations[key] = configuration
  }

  fun shareCommonConfiguration(
    scope: String?,
    configuration: TestedExtension.(Project) -> Unit
  ) {
    val key = scope ?: MainScope
    commonConfigurations[key] = configuration
  }

  fun getAppConfiguration(scope: String?) = appConfigurations[scope ?: MainScope]
  fun getLibConfiguration(scope: String?) = libConfigurations[scope ?: MainScope]
  fun getCommonConfiguration(scope: String?) = commonConfigurations[scope ?: MainScope]
}