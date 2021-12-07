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
@file:Suppress
// import com.meowool.gradle.toolkit.internal.InternalGradleToolkitApi
// import org.gradle.api.Project
// import org.gradle.api.initialization.dsl.ScriptHandler
// import org.gradle.api.provider.Provider
// import org.gradle.kotlin.dsl.GradleDsl
// import org.gradle.kotlin.dsl.ScriptHandlerScope
// import org.gradle.kotlin.dsl.buildscript
// import org.gradle.plugin.use.PluginDependenciesSpec
// import org.gradle.plugin.use.PluginDependency
// import org.gradle.plugin.use.PluginDependencySpec
//
// /**
// * Configures the build script classpath for this project.
// *
// * Unlike [buildscript] {}, the classloader of this block is more secure, and the original classloader lacks
// * some classes added by third-party plugins (such as dependency-mapper).
// */
// fun Project.buildscriptToolkit(action: ScriptHandlerScope.() -> Unit) = beforeEvaluate {
//  project.buildscript.configureWith(action)
// }
//
// /**
// * Configures the build script classpath for this project.
// *
// * Unlike [plugins] {}, the classloader of this block is more secure, and the original classloader lacks
// * some classes added by third-party plugins (such as dependency-mapper).
// */
// fun Project.pluginsToolkit(action: PluginDependenciesSpec.() -> Unit) = beforeEvaluate {
//  project.buildscript.configureWith {
//    dependencies.classpath(Plugin)
//  }
//  project.buildscript.configureWith(action)
// }
//
// private inline fun ScriptHandler.configureWith(block: ScriptHandlerScope.() -> Unit) {
//  ScriptHandlerScope.of(this).block()
// }
//
//
// private class PluginDependenciesToolkitSpecScope : PluginDependenciesSpec {
//
//  override fun id(id: String): PluginDependencySpec =
//    plugins.id(id)
//
//  @Suppress("UnstableApiUsage")
//  override fun alias(notation: Provider<PluginDependency>) =
//    throw UnsupportedOperationException("`pluginsToolkit {}` does not support the declaration of alias, please use the `plugins {}` to declare")
// }
