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
import annotation.InternalGradleToolkitApi
import extension.GradleToolkitExtension
import extension.RootGradleToolkitExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

@InternalGradleToolkitApi
val Project.rootExtension: RootGradleToolkitExtension
  get() = checkRootBootstrap()

private fun Project.checkRootBootstrap(): RootGradleToolkitExtension {
  val extension = rootProject.extensions.findByType<GradleToolkitExtension>()
  check(extension != null) {
    // GradleToolkitCore.bootstrap
    "'gradle-toolkit' bootstrap has not started, please apply the GradleToolkit plugin in root settings.gradle(.kts) or build.gradle(.kts) first."
  }
  check(extension is RootGradleToolkitExtension) { "Illegal root extension!" }
  return extension
}
