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

private fun Project.init(scope: String? = null) {
  // Either app or lib should import kotlin-android
  ensureApplyPlugin("kotlin-android")

  android {
    loadAndroidPresets()

    // Use shared common-configuration
    rootExtension.data.filterIsInstance<AndroidConfigurationStore>()
      .firstOrNull()
      ?.getCommonConfiguration(scope)
      ?.invoke(this, this@init)
  }
}

internal fun Project.android(configuration: TestedExtension.() -> Unit) {
  requireApplyPlugin()
  extensions.getByName("android").apply {
    this as TestedExtension
    configuration()
  }
}

/**
 * The extension [configuration] of the Android-Application project.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.androidApp(scope: String? = null, configuration: BaseAppModuleExtension.() -> Unit) {
  ensureApplyPlugin("android")
  init(scope)
  android {
    this as BaseAppModuleExtension

    // Use shared application-configuration
    rootExtension.data.filterIsInstance<AndroidConfigurationStore>()
      .firstOrNull()
      ?.getAppConfiguration(scope)
      ?.invoke(this, this@androidApp)

    configuration()
  }
}

/**
 * The extension [configuration] of the Android-Library project.
 */
fun Project.androidLib(scope: String? = null, configuration: LibraryExtension.() -> Unit) {
  ensureApplyPlugin("android-library")
  init(scope)
  android {
    this as LibraryExtension

    // Use shared library-configuration
    rootExtension.data.filterIsInstance<AndroidConfigurationStore>()
      .firstOrNull()
      ?.getLibConfiguration(scope)
      ?.invoke(this, this@androidLib)

    configuration()
  }
}
