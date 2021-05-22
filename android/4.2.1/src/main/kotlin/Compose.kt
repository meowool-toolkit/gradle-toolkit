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
import com.android.build.api.dsl.ComposeOptions
import org.gradle.api.Project

/**
 * Enables the 'Compose' for the current android project
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.enableCompose(configuration: ComposeOptions.() -> Unit = {}) {
  android {
    buildFeatures.compose = true
    configurations.configureEach {
      val kotlinDependency = allDependencies.find {
        it.group?.startsWith("org.jetbrains.kotlin") == true && it.version != null
      }
      val composeDependency = allDependencies.find {
        it.group?.startsWith("androidx.compose") == true && it.version != null
      }
      composeOptions {
        // We use the first kotlin-dependency version found as the compose-compiler version
        kotlinDependency?.version?.let { kotlinCompilerVersion = it }
        // We use the first compose-dependency version found as the compose-compiler-extension version
        composeDependency?.version?.let { kotlinCompilerExtensionVersion = it }
        configuration()
      }
    }
  }
}