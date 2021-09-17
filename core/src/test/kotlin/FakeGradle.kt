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

 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
import groovy.lang.Closure
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.StartParameter
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.initialization.IncludedBuild
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.plugins.ObjectConfigurationAction
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.plugins.PluginManager
import org.gradle.api.services.BuildServiceRegistry
import org.gradle.kotlin.dsl.apply
import java.io.File

/**
 * @author 凛 (https://github.com/RinOrz)
 */
internal class FakeGradle(private val rootProject: Project) : Gradle {
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

  override fun getGradleVersion(): String {
    TODO("Not yet implemented")
  }

  override fun getGradleUserHomeDir(): File {
    TODO("Not yet implemented")
  }

  override fun getGradleHomeDir(): File? {
    TODO("Not yet implemented")
  }

  override fun getParent(): Gradle? {
    TODO("Not yet implemented")
  }

  override fun getRootProject(): Project {
    return rootProject
  }

  override fun rootProject(action: Action<in Project>) {
    rootProject.apply(action)
  }

  override fun allprojects(action: Action<in Project>) {
    TODO("Not yet implemented")
  }

  override fun getTaskGraph(): TaskExecutionGraph {
    TODO("Not yet implemented")
  }

  override fun getStartParameter(): StartParameter {
    TODO("Not yet implemented")
  }

  override fun addProjectEvaluationListener(listener: ProjectEvaluationListener): ProjectEvaluationListener {
    TODO("Not yet implemented")
  }

  override fun removeProjectEvaluationListener(listener: ProjectEvaluationListener) {
    TODO("Not yet implemented")
  }

  override fun beforeProject(closure: Closure<*>) {
    TODO("Not yet implemented")
  }

  override fun beforeProject(action: Action<in Project>) {
    TODO("Not yet implemented")
  }

  override fun afterProject(closure: Closure<*>) {
    TODO("Not yet implemented")
  }

  override fun afterProject(action: Action<in Project>) {
    TODO("Not yet implemented")
  }

  override fun beforeSettings(closure: Closure<*>) {
    TODO("Not yet implemented")
  }

  override fun beforeSettings(action: Action<in Settings>) {
    TODO("Not yet implemented")
  }

  override fun settingsEvaluated(closure: Closure<*>) {
    TODO("Not yet implemented")
  }

  override fun settingsEvaluated(action: Action<in Settings>) {
    TODO("Not yet implemented")
  }

  override fun projectsLoaded(closure: Closure<*>) {
    TODO("Not yet implemented")
  }

  override fun projectsLoaded(action: Action<in Gradle>) {
    TODO("Not yet implemented")
  }

  override fun projectsEvaluated(closure: Closure<*>) {
    TODO("Not yet implemented")
  }

  override fun projectsEvaluated(action: Action<in Gradle>) {
    TODO("Not yet implemented")
  }

  override fun buildFinished(closure: Closure<*>) {
    TODO("Not yet implemented")
  }

  override fun buildFinished(action: Action<in BuildResult>) {
    TODO("Not yet implemented")
  }

  override fun addBuildListener(buildListener: BuildListener) {
    TODO("Not yet implemented")
  }

  override fun addListener(listener: Any) {
    TODO("Not yet implemented")
  }

  override fun removeListener(listener: Any) {
    TODO("Not yet implemented")
  }

  override fun useLogger(logger: Any) {
    TODO("Not yet implemented")
  }

  override fun getGradle(): Gradle {
    TODO("Not yet implemented")
  }

  override fun getSharedServices(): BuildServiceRegistry {
    TODO("Not yet implemented")
  }

  override fun getIncludedBuilds(): MutableCollection<IncludedBuild> {
    TODO("Not yet implemented")
  }

  override fun includedBuild(name: String): IncludedBuild {
    TODO("Not yet implemented")
  }
}
