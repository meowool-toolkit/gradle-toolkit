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
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.accessors.runtime.addDependencyTo
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.project
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

/**
 * Adds a dependency to the 'api' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @param dependencyConfiguration expression to use to configure the dependency.
 * @return The dependency.
 *
 * @see [addDependencyTo]
 */
fun DependencyHandler.api(
  dependencyNotation: CharSequence,
  dependencyConfiguration: Action<ExternalModuleDependency>
): ExternalModuleDependency = addDependencyTo(
  this, "api", dependencyNotation.toString(), dependencyConfiguration
)

/**
 * Adds a project dependency to the 'api' configuration.
 *
 * @param projectPath project path for the dependency to be added.
 * @param dependencyConfiguration expression to use to configure the dependency.
 * @return The dependency.
 *
 * @see [addDependencyTo]
 */
fun DependencyHandler.apiProject(
  projectPath: CharSequence,
  dependencyConfiguration: Action<ExternalModuleDependency>
): ExternalModuleDependency = addDependencyTo(
  this, "api", project(projectPath.toString()), dependencyConfiguration
)

/**
 * Adds a project dependency to the 'api' configuration.
 *
 * @param projectPath project path for the dependencies to be added.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.apiProject(projectPath: CharSequence): Dependency? =
  add("api", project(projectPath.toString()))

/**
 * Adds a dependencies to the 'api' configuration.
 *
 * @param dependenciesNotation notation for the dependencies to be added.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.apiOf(vararg dependenciesNotation: Any): List<Dependency?> =
  dependenciesNotation.map { add("api", it) }

/**
 * Adds a dependencies to the 'api' configuration.
 *
 * @param dependenciesNotation notation for the dependencies to be added.
 * @param allDependenciesConfiguration expression to use to configure the all dependencies.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.apiOf(
  vararg dependenciesNotation: CharSequence,
  allDependenciesConfiguration: Action<ExternalModuleDependency>
): List<ExternalModuleDependency> = dependenciesNotation.map {
  addDependencyTo(this, "api", it.toString(), allDependenciesConfiguration)
}

/**
 * Adds some project dependencies to the 'api' configuration.
 *
 * @param projectPaths project paths for the dependencies to be added.
 *
 * @see [DependencyHandler.add]
 * @see [DependencyHandler.project]
 */
fun DependencyHandler.apiProjects(vararg projectPaths: String): List<Dependency?> =
  projectPaths.map { add("api", project(it)) }

/**
 * Adds some project dependencies to the 'api' configuration.
 *
 * @param projectPaths project paths for the dependencies to be added.
 * @param allDependenciesConfiguration expression to use to configure the all dependencies.
 *
 * @see [DependencyHandler.add]
 * @see [DependencyHandler.project]
 */
fun DependencyHandler.apiProjects(
  vararg projectPaths: String,
  allDependenciesConfiguration: ModuleDependency.() -> Unit
): List<Dependency> = projectPaths.map {
  add("api", project(it), allDependenciesConfiguration)
}

/**
 * Adds some files dependencies to the 'api' configuration.
 *
 * @param directories the directories where the files for the dependencies to be added.
 * @param include filter files to be imported (import all files in the folder by default).
 *
 * @see [Project.fileTree]
 * @see [Project.dependencies]
 * @see [DependencyHandler.add]
 */
fun Project.apiFiles(
  vararg directories: String,
  include: List<String> = listOf("*")
): List<Dependency?> = directories.map {
  dependencies.add("api", fileTree(mapOf("dir" to it, "include" to include)))
}

/**
 * Adds some jars dependencies to the 'api' configuration.
 *
 * @param jarDirectory project jar folder path for the dependencies to be added.
 *
 * @see [Project.fileTree]
 * @see [Project.dependencies]
 * @see [DependencyHandler.add]
 */
fun Project.apiJars(vararg jarDirectory: String = arrayOf("libs")): List<Dependency?> =
  jarDirectory.map { apiFiles(it, "*.jar").first() }

// // kotlin-multiplatform

/**
 * Adds a dependency to the 'api' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @param dependencyConfiguration expression to use to configure the dependency.
 * @return The dependency.
 */
fun KotlinDependencyHandler.api(
  dependencyNotation: CharSequence,
  dependencyConfiguration: ExternalModuleDependency.() -> Unit
): ExternalModuleDependency = api(dependencyNotation.toString(), dependencyConfiguration)

/**
 * Adds a dependencies to the 'api' configuration.
 *
 * @param dependenciesNotation notation for the dependencies to be added.
 */
fun KotlinDependencyHandler.apiOf(vararg dependenciesNotation: Any): List<Dependency?> =
  dependenciesNotation.map { api(it) }

/**
 * Adds a dependencies to the 'api' configuration.
 *
 * @param dependenciesNotation notation for the dependencies to be added.
 * @param allDependenciesConfiguration expression to use to configure the all dependencies.
 */
fun KotlinDependencyHandler.apiOf(
  vararg dependenciesNotation: CharSequence,
  allDependenciesConfiguration: ExternalModuleDependency.() -> Unit
): List<ExternalModuleDependency> = dependenciesNotation.map {
  api(it.toString(), allDependenciesConfiguration)
}

/**
 * Adds some project dependencies to the 'api' configuration.
 *
 * @param projectPaths project paths for the dependencies to be added.
 */
fun KotlinDependencyHandler.apiProjects(vararg projectPaths: String): List<Dependency?> =
  projectPaths.map { api(project(it)) }

/**
 * Adds some project dependencies to the 'api' configuration.
 *
 * @param projectPaths project paths for the dependencies to be added.
 * @param allDependenciesConfiguration expression to use to configure the all dependencies.
 */
fun KotlinDependencyHandler.apiProjects(
  vararg projectPaths: String,
  allDependenciesConfiguration: ModuleDependency.() -> Unit
): List<Dependency> = projectPaths.map {
  api(project(it), allDependenciesConfiguration)
}
