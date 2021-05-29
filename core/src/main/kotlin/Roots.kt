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
import annotation.InternalGradleDslXApi
import extension.GradleDslExtension
import extension.RootGradleDslExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

@InternalGradleDslXApi
inline val Project.rootExtension get() = checkRootBootstrap()

@InternalGradleDslXApi
fun Project.checkRootBootstrap(): GradleDslExtension {
  val extension = rootProject.extensions.findByType<GradleDslExtension>()
  check(extension != null) {
    "gradle dsl x bootstrap has not started, please apply the GradleDslX plugin in root settings.gradle(.kts) or build.gradle(.kts) first."
  }
  check(extension is RootGradleDslExtension) { "Illegal root extension!" }
  return extension
}
