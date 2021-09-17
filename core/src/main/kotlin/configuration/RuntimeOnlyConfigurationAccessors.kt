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
 * Adds a dependency to the 'runtimeOnly' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @param dependencyConfiguration expression to use to configure the dependency.
 * @return The dependency.
 *
 * @see [addDependencyTo]
 */
fun DependencyHandler.runtimeOnly(
  dependencyNotation: CharSequence,
  dependencyConfiguration: Action<ExternalModuleDependency>
): ExternalModuleDependency = addDependencyTo(
  this, "runtimeOnly", dependencyNotation.toString(), dependencyConfiguration
)

/**
 * Adds a project dependency to the 'runtimeOnly' configuration.
 *
 * @param projectPath project path for the dependency to be added.
 * @param dependencyConfiguration expression to use to configure the dependency.
 * @return The dependency.
 *
 * @see [addDependencyTo]
 */
fun DependencyHandler.runtimeOnlyProject(
  projectPath: CharSequence,
  dependencyConfiguration: Action<ExternalModuleDependency>
): ExternalModuleDependency = addDependencyTo(
  this, "runtimeOnly", project(projectPath.toString()), dependencyConfiguration
)

/**
 * Adds a project dependency to the 'runtimeOnly' configuration.
 *
 * @param projectPath project path for the dependencies to be added.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.runtimeOnlyProject(projectPath: CharSequence): Dependency? =
  add("runtimeOnly", project(projectPath.toString()))

/**
 * Adds a dependencies to the 'runtimeOnly' configuration.
 *
 * @param dependenciesNotation notation for the dependencies to be added.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.runtimeOnlyOf(vararg dependenciesNotation: Any): List<Dependency?> =
  dependenciesNotation.map { add("runtimeOnly", it) }

/**
 * Adds a dependencies to the 'runtimeOnly' configuration.
 *
 * @param dependenciesNotation notation for the dependencies to be added.
 * @param allDependenciesConfiguration expression to use to configure the all dependencies.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.runtimeOnlyOf(
  vararg dependenciesNotation: CharSequence,
  allDependenciesConfiguration: Action<ExternalModuleDependency>
): List<ExternalModuleDependency> = dependenciesNotation.map {
  addDependencyTo(this, "runtimeOnly", it.toString(), allDependenciesConfiguration)
}

/**
 * Adds some project dependencies to the 'runtimeOnly' configuration.
 *
 * @param projectPaths project paths for the dependencies to be added.
 *
 * @see [DependencyHandler.add]
 * @see [DependencyHandler.project]
 */
fun DependencyHandler.runtimeOnlyProjects(vararg projectPaths: String): List<Dependency?> =
  projectPaths.map { add("runtimeOnly", project(it)) }

/**
 * Adds some project dependencies to the 'runtimeOnly' configuration.
 *
 * @param projectPaths project paths for the dependencies to be added.
 * @param allDependenciesConfiguration expression to use to configure the all dependencies.
 *
 * @see [DependencyHandler.add]
 * @see [DependencyHandler.project]
 */
fun DependencyHandler.runtimeOnlyProjects(
  vararg projectPaths: String,
  allDependenciesConfiguration: ModuleDependency.() -> Unit
): List<Dependency> = projectPaths.map {
  add("runtimeOnly", project(it), allDependenciesConfiguration)
}

/**
 * Adds some files dependencies to the 'runtimeOnly' configuration.
 *
 * @param directories the directories where the files for the dependencies to be added.
 * @param include filter files to be imported (import all files in the folder by default).
 *
 * @see [Project.fileTree]
 * @see [Project.dependencies]
 * @see [DependencyHandler.add]
 */
fun Project.runtimeOnlyFiles(
  vararg directories: String,
  include: List<String> = listOf("*")
): List<Dependency?> = directories.map {
  dependencies.add("runtimeOnly", fileTree(mapOf("dir" to it, "include" to include)))
}

/**
 * Adds some jars dependencies to the 'runtimeOnly' configuration.
 *
 * @param jarDirectory project jar folder path for the dependencies to be added.
 *
 * @see [Project.fileTree]
 * @see [Project.dependencies]
 * @see [DependencyHandler.add]
 */
fun Project.runtimeOnlyJars(vararg jarDirectory: String = arrayOf("libs")): List<Dependency?> =
  jarDirectory.map { runtimeOnlyFiles(it, "*.jar").first() }

// // kotlin-multiplatform

/**
 * Adds a dependency to the 'runtimeOnly' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @param dependencyConfiguration expression to use to configure the dependency.
 * @return The dependency.
 */
fun KotlinDependencyHandler.runtimeOnly(
  dependencyNotation: CharSequence,
  dependencyConfiguration: ExternalModuleDependency.() -> Unit
): ExternalModuleDependency = runtimeOnly(dependencyNotation.toString(), dependencyConfiguration)

/**
 * Adds a dependencies to the 'runtimeOnly' configuration.
 *
 * @param dependenciesNotation notation for the dependencies to be added.
 */
fun KotlinDependencyHandler.runtimeOnlyOf(vararg dependenciesNotation: Any): List<Dependency?> =
  dependenciesNotation.map { runtimeOnly(it) }

/**
 * Adds a dependencies to the 'runtimeOnly' configuration.
 *
 * @param dependenciesNotation notation for the dependencies to be added.
 * @param allDependenciesConfiguration expression to use to configure the all dependencies.
 */
fun KotlinDependencyHandler.runtimeOnlyOf(
  vararg dependenciesNotation: CharSequence,
  allDependenciesConfiguration: ExternalModuleDependency.() -> Unit
): List<ExternalModuleDependency> = dependenciesNotation.map {
  runtimeOnly(it.toString(), allDependenciesConfiguration)
}

/**
 * Adds some project dependencies to the 'runtimeOnly' configuration.
 *
 * @param projectPaths project paths for the dependencies to be added.
 */
fun KotlinDependencyHandler.runtimeOnlyProjects(vararg projectPaths: String): List<Dependency?> =
  projectPaths.map { runtimeOnly(project(it)) }

/**
 * Adds some project dependencies to the 'runtimeOnly' configuration.
 *
 * @param projectPaths project paths for the dependencies to be added.
 * @param allDependenciesConfiguration expression to use to configure the all dependencies.
 */
fun KotlinDependencyHandler.runtimeOnlyProjects(
  vararg projectPaths: String,
  allDependenciesConfiguration: ModuleDependency.() -> Unit
): List<Dependency> = projectPaths.map {
  runtimeOnly(project(it), allDependenciesConfiguration)
}
