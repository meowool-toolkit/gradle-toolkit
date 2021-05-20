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
import extension.GradleDslExtension
import extension.RootGradleDslExtension
import extension.RootGradleDslExtensionImpl
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType

/**
 * Import shared lazy dependencies block by [scope] name.
 * Note that this will use the scope name specified by the current project as much as possible.
 *
 * @see RootGradleDslExtension.shareLazyDependencies
 * @see GradleDslExtension.scope
 */
fun Project.importLazyDependencies(scope: String? = null) = dependencies {
  val key = scope
    ?: extensions.findByType<GradleDslExtension>()?.scope
    ?: MainScope

  (rootExtension as? RootGradleDslExtensionImpl)
    ?.sharedLazyDependencies
    ?.get(key)
    ?.invoke(this)
}
