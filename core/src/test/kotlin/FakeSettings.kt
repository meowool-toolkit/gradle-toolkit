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
import groovy.lang.Closure
import org.gradle.StartParameter
import org.gradle.api.Action
import org.gradle.api.initialization.ConfigurableIncludedBuild
import org.gradle.api.initialization.ProjectDescriptor
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.api.initialization.resolve.DependencyResolutionManagement
import org.gradle.api.invocation.Gradle
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.ObjectConfigurationAction
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.ProviderFactory
import org.gradle.caching.configuration.BuildCacheConfiguration
import org.gradle.plugin.management.PluginManagementSpec
import org.gradle.vcs.SourceControl
import java.io.File

/**
 * @author 凛 (RinOrz)
 */
internal class FakeSettings(private val gradle: Gradle) : Settings {
  override fun getPlugins(): PluginContainer {
    TODO("Not yet implemented")
  }

  override fun apply(closure: Closure<*>) {
    TODO("Not yet implemented")
  }

  override fun apply(action: Action<in ObjectConfigurationAction>) {
    TODO("Not yet implemented")
  }

  override fun apply(options: MutableMap<String, *>) {
    TODO("Not yet implemented")
  }

  override fun getPluginManager(): PluginManager {
    TODO("Not yet implemented")
  }

  override fun getExtensions(): ExtensionContainer {
    TODO("Not yet implemented")
  }

  val includedProjectPaths = mutableListOf<String>()

  override fun include(vararg projectPaths: String) {
    includedProjectPaths.addAll(projectPaths)
  }

  override fun includeFlat(vararg projectNames: String?) {
    TODO("Not yet implemented")
  }

  override fun getSettings(): Settings {
    TODO("Not yet implemented")
  }

  override fun getBuildscript(): ScriptHandler {
    TODO("Not yet implemented")
  }

  override fun getSettingsDir(): File {
    TODO("Not yet implemented")
  }

  lateinit var _rootDir: File
  override fun getRootDir(): File = _rootDir

  override fun getRootProject(): ProjectDescriptor {
    TODO("Not yet implemented")
  }

  override fun project(path: String): ProjectDescriptor {
    TODO("Not yet implemented")
  }

  override fun project(projectDir: File): ProjectDescriptor {
    TODO("Not yet implemented")
  }

  override fun findProject(path: String): ProjectDescriptor? {
    TODO("Not yet implemented")
  }

  override fun findProject(projectDir: File): ProjectDescriptor? {
    TODO("Not yet implemented")
  }

  override fun getStartParameter(): StartParameter {
    TODO("Not yet implemented")
  }

  override fun getProviders(): ProviderFactory {
    TODO("Not yet implemented")
  }

  override fun getGradle(): Gradle = gradle

  override fun includeBuild(rootProject: Any) {
    TODO("Not yet implemented")
  }

  override fun includeBuild(rootProject: Any, configuration: Action<ConfigurableIncludedBuild>) {
    TODO("Not yet implemented")
  }

  override fun getBuildCache(): BuildCacheConfiguration {
    TODO("Not yet implemented")
  }

  override fun buildCache(action: Action<in BuildCacheConfiguration>) {
    TODO("Not yet implemented")
  }

  override fun pluginManagement(pluginManagementSpec: Action<in PluginManagementSpec>) {
    TODO("Not yet implemented")
  }

  override fun getPluginManagement(): PluginManagementSpec {
    TODO("Not yet implemented")
  }

  override fun sourceControl(configuration: Action<in SourceControl>) {
    TODO("Not yet implemented")
  }

  override fun getSourceControl(): SourceControl {
    TODO("Not yet implemented")
  }

  override fun enableFeaturePreview(name: String) {
    TODO("Not yet implemented")
  }

  override fun dependencyResolutionManagement(dependencyResolutionConfiguration: Action<in DependencyResolutionManagement>) {
    TODO("Not yet implemented")
  }

  override fun getDependencyResolutionManagement(): DependencyResolutionManagement {
    TODO("Not yet implemented")
  }
}
