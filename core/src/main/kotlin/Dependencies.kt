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
@file:Suppress("ReplaceGetOrSet", "SpellCheckingInspection", "UnstableApiUsage")

import org.gradle.api.Incubating
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencyConstraint
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.DependencyConstraintHandler
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyConstraintHandlerScope
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.gradleKotlinDsl

/**
 * Toolkit the dependencies of [project].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class DependencyHandlerToolkit(
  val project: Project,
  private val dependencies: DependencyHandlerScope
) : DependencyHandler by dependencies {

  /**
   * Creates a dependency on the API of the current version of the Gradle Kotlin DSL.
   *
   * Includes the Kotlin and Gradle APIs.
   *
   * @see Project.gradleKotlinDsl
   */
  fun gradleKotlinDsl(): Dependency = project.gradleKotlinDsl()

  /**
   * Configures dependency constraint for this project.
   *
   * @param configureAction the action to use to configure module metadata
   *
   * @since 6.3
   */
  fun constraints(configureAction: DependencyConstraintHandlerScope.() -> Unit) {
    dependencies.constraints { configureAction(DependencyConstraintHandlerScope.of(this)) }
  }

  /**
   * Adds a dependency to the given configuration.
   *
   * @param dependencyNotation notation for the dependency to be added.
   * @return The dependency.
   * @see [DependencyHandler.add]
   */
  operator fun String.invoke(dependencyNotation: Any): Dependency? =
    dependencies.add(this, dependencyNotation)

  /**
   * Adds a dependency to the given configuration.
   *
   * @param dependencyNotation notation for the dependency to be added.
   * @param dependencyConfiguration expression to use to configure the dependency.
   * @return The dependency.
   * @see [DependencyHandler.add]
   */
  operator fun String.invoke(
    dependencyNotation: String,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit
  ): ExternalModuleDependency =
    dependencies.add(this, dependencyNotation, dependencyConfiguration)

  /**
   * Adds a dependency to the given configuration.
   *
   * @param group the group of the module to be added as a dependency.
   * @param name the name of the module to be added as a dependency.
   * @param version the optional version of the module to be added as a dependency.
   * @param configuration the optional configuration of the module to be added as a dependency.
   * @param classifier the optional classifier of the module artifact to be added as a dependency.
   * @param ext the optional extension of the module artifact to be added as a dependency.
   * @return The dependency.
   *
   * @see [DependencyHandler.add]
   */
  operator fun String.invoke(
    group: String,
    name: String,
    version: String? = null,
    configuration: String? = null,
    classifier: String? = null,
    ext: String? = null
  ): ExternalModuleDependency =
    dependencies.create(group, name, version, configuration, classifier, ext).apply { add(this@invoke, this) }

  /**
   * Adds a dependency to the given configuration.
   *
   * @param group the group of the module to be added as a dependency.
   * @param name the name of the module to be added as a dependency.
   * @param version the optional version of the module to be added as a dependency.
   * @param configuration the optional configuration of the module to be added as a dependency.
   * @param classifier the optional classifier of the module artifact to be added as a dependency.
   * @param ext the optional extension of the module artifact to be added as a dependency.
   * @param dependencyConfiguration expression to use to configure the dependency.
   * @return The dependency.
   *
   * @see [DependencyHandler.create]
   * @see [DependencyHandler.add]
   */
  operator fun String.invoke(
    group: String,
    name: String,
    version: String? = null,
    configuration: String? = null,
    classifier: String? = null,
    ext: String? = null,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit
  ): ExternalModuleDependency =
    dependencies.add(this, create(group, name, version, configuration, classifier, ext), dependencyConfiguration)

  /**
   * Adds a dependency to the given configuration.
   *
   * @param dependency dependency to be added.
   * @param dependencyConfiguration expression to use to configure the dependency.
   * @return The dependency.
   *
   * @see [DependencyHandler.add]
   */
  operator fun <T : ModuleDependency> String.invoke(dependency: T, dependencyConfiguration: T.() -> Unit): T =
    dependencies.add(this, dependency, dependencyConfiguration)

  /**
   * Adds a dependency to the given configuration.
   *
   * @param dependencyNotation notation for the dependency to be added.
   * @return The dependency.
   * @see [DependencyHandler.add]
   */
  operator fun Configuration.invoke(dependencyNotation: Any): Dependency? =
    add(name, dependencyNotation)

  /**
   * Adds a dependency to the given configuration.
   *
   * @param dependencyNotation notation for the dependency to be added.
   * @param dependencyConfiguration expression to use to configure the dependency.
   * @return The dependency.
   * @see [DependencyHandler.add]
   */
  operator fun Configuration.invoke(
    dependencyNotation: String,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit
  ): ExternalModuleDependency =
    add(name, dependencyNotation, dependencyConfiguration)

  /**
   * Adds a dependency to the given configuration.
   *
   * @param group the group of the module to be added as a dependency.
   * @param name the name of the module to be added as a dependency.
   * @param version the optional version of the module to be added as a dependency.
   * @param configuration the optional configuration of the module to be added as a dependency.
   * @param classifier the optional classifier of the module artifact to be added as a dependency.
   * @param ext the optional extension of the module artifact to be added as a dependency.
   * @return The dependency.
   *
   * @see [DependencyHandler.add]
   */
  operator fun Configuration.invoke(
    group: String,
    name: String,
    version: String? = null,
    configuration: String? = null,
    classifier: String? = null,
    ext: String? = null
  ): ExternalModuleDependency =
    create(group, name, version, configuration, classifier, ext).apply { add(this@invoke.name, this) }

  /**
   * Adds a dependency to the given configuration.
   *
   * @param group the group of the module to be added as a dependency.
   * @param name the name of the module to be added as a dependency.
   * @param version the optional version of the module to be added as a dependency.
   * @param configuration the optional configuration of the module to be added as a dependency.
   * @param classifier the optional classifier of the module artifact to be added as a dependency.
   * @param ext the optional extension of the module artifact to be added as a dependency.
   * @param dependencyConfiguration expression to use to configure the dependency.
   * @return The dependency.
   *
   * @see [DependencyHandler.create]
   * @see [DependencyHandler.add]
   */
  operator fun Configuration.invoke(
    group: String,
    name: String,
    version: String? = null,
    configuration: String? = null,
    classifier: String? = null,
    ext: String? = null,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit
  ): ExternalModuleDependency =
    add(this.name, create(group, name, version, configuration, classifier, ext), dependencyConfiguration)

  /**
   * Adds a dependency to the given configuration.
   *
   * @param dependency dependency to be added.
   * @param dependencyConfiguration expression to use to configure the dependency.
   * @return The dependency.
   *
   * @see [DependencyHandler.add]
   */
  operator fun <T : ModuleDependency> Configuration.invoke(dependency: T, dependencyConfiguration: T.() -> Unit): T =
    add(name, dependency, dependencyConfiguration)

  /**
   * Adds a dependency provider to the given configuration.
   *
   * @param dependency the dependency provider to be added.
   * @param dependencyConfiguration the configuration to be applied to the dependency
   *
   * @see [DependencyHandler.addProvider]
   * @since 7.0
   */
  @Incubating
  operator fun <T : Any> Configuration.invoke(
    dependency: Provider<T>,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit
  ) =
    addProvider(name, dependency, dependencyConfiguration)

  /**
   * Adds a dependency provider to the given configuration.
   *
   * @param dependency the dependency provider to be added.
   *
   * @see [DependencyHandler.addProvider]
   * @since 7.0
   */
  @Incubating
  operator fun <T : Any> Configuration.invoke(dependency: Provider<T>) =
    addProvider(name, dependency)

  /**
   * Adds a dependency provider to the given configuration.
   *
   * @param dependency the dependency provider to be added.
   * @param dependencyConfiguration the configuration to be applied to the dependency
   *
   * @see [DependencyHandler.addProvider]
   * @since 7.0
   */
  @Incubating
  operator fun <T : Any> String.invoke(
    dependency: Provider<T>,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit
  ) =
    addProvider(this, dependency, dependencyConfiguration)

  /**
   * Adds a dependency provider to the given configuration.
   *
   * @param dependency the dependency provider to be added.
   *
   * @see [DependencyHandler.addProvider]
   * @since 7.0
   */
  @Incubating
  operator fun <T : Any> String.invoke(dependency: Provider<T>) =
    addProvider(this, dependency)

  // ////////////////////////////////////////////////////////////////////////
  // //                         For root project                         ////
  // ////////////////////////////////////////////////////////////////////////

  /**
   * The script classpath configuration.
   */
  val NamedDomainObjectContainer<Configuration>.classpath: NamedDomainObjectProvider<Configuration>
    get() = named(ScriptHandler.CLASSPATH_CONFIGURATION)

  /**
   * Adds a dependency to the script classpath.
   *
   * @param dependencyNotation notation for the dependency to be added.
   * @return The dependency.
   *
   * @see [DependencyHandler.add]
   */
  fun DependencyHandler.classpath(dependencyNotation: Any): Dependency? =
    add(ScriptHandler.CLASSPATH_CONFIGURATION, dependencyNotation)

  /**
   * Adds a dependency to the script classpath.
   *
   * @param dependencyNotation notation for the dependency to be added.
   * @param dependencyConfiguration expression to use to configure the dependency.
   * @return The dependency.
   *
   * @see [DependencyHandler.add]
   */
  fun DependencyHandler.classpath(
    dependencyNotation: String,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit
  ): ExternalModuleDependency = add(ScriptHandler.CLASSPATH_CONFIGURATION, dependencyNotation, dependencyConfiguration)

  /**
   * Adds a dependency to the script classpath.
   *
   * @param group the group of the module to be added as a dependency.
   * @param name the name of the module to be added as a dependency.
   * @param version the optional version of the module to be added as a dependency.
   * @param configuration the optional configuration of the module to be added as a dependency.
   * @param classifier the optional classifier of the module artifact to be added as a dependency.
   * @param ext the optional extension of the module artifact to be added as a dependency.
   * @return The dependency.
   *
   * @see [DependencyHandler.add]
   */
  fun DependencyHandler.classpath(
    group: String,
    name: String,
    version: String? = null,
    configuration: String? = null,
    classifier: String? = null,
    ext: String? = null
  ): ExternalModuleDependency = create(group, name, version, configuration, classifier, ext).also {
    add(ScriptHandler.CLASSPATH_CONFIGURATION, it)
  }

  /**
   * Adds a dependency to the script classpath.
   *
   * @param group the group of the module to be added as a dependency.
   * @param name the name of the module to be added as a dependency.
   * @param version the optional version of the module to be added as a dependency.
   * @param configuration the optional configuration of the module to be added as a dependency.
   * @param classifier the optional classifier of the module artifact to be added as a dependency.
   * @param ext the optional extension of the module artifact to be added as a dependency.
   * @param dependencyConfiguration expression to use to configure the dependency.
   * @return The dependency.
   *
   * @see [DependencyHandler.create]
   * @see [DependencyHandler.add]
   */
  fun DependencyHandler.classpath(
    group: String,
    name: String,
    version: String? = null,
    configuration: String? = null,
    classifier: String? = null,
    ext: String? = null,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit
  ): ExternalModuleDependency = create(group, name, version, configuration, classifier, ext).also {
    add(ScriptHandler.CLASSPATH_CONFIGURATION, it, dependencyConfiguration)
  }

  /**
   * Adds a dependency to the script classpath.
   *
   * @param dependency dependency to be added.
   * @param dependencyConfiguration expression to use to configure the dependency.
   * @return The dependency.
   *
   * @see [DependencyHandler.add]
   */
  fun <T : ModuleDependency> DependencyHandler.classpath(
    dependency: T,
    dependencyConfiguration: T.() -> Unit
  ): T = add(ScriptHandler.CLASSPATH_CONFIGURATION, dependency, dependencyConfiguration)

  /**
   * Adds a dependency constraint to the script classpath configuration.
   *
   * @param dependencyConstraintNotation the dependency constraint notation
   *
   * @return the added dependency constraint
   *
   * @see [DependencyConstraintHandler.add]
   * @since 5.0
   */
  fun DependencyConstraintHandler.classpath(dependencyConstraintNotation: Any): DependencyConstraint? =
    add(ScriptHandler.CLASSPATH_CONFIGURATION, dependencyConstraintNotation)

  /**
   * Adds a dependency constraint to the script classpath configuration.
   *
   * @param dependencyConstraintNotation the dependency constraint notation
   * @param configuration the block to use to configure the dependency constraint
   *
   * @return the added dependency constraint
   *
   * @see [DependencyConstraintHandler.add]
   * @since 5.0
   */
  fun DependencyConstraintHandler.classpath(
    dependencyConstraintNotation: Any,
    configuration: DependencyConstraint.() -> Unit
  ): DependencyConstraint? =
    add(ScriptHandler.CLASSPATH_CONFIGURATION, dependencyConstraintNotation, configuration)
}

/**
 * Configures the repositories for all projects.
 *
 * Executes the given [configuration] block against the [RepositoryHandler] for all projects.
 *
 * @see Project.allprojects
 * @see Project.dependencies
 */
fun Project.alldependencies(configuration: DependencyHandlerToolkit.() -> Unit) = allprojects {
  afterEvaluate {
    DependencyHandlerToolkit(project, DependencyHandlerScope.of(dependencies)).configuration()
  }
}

/**
 * Configures the repositories for all sub-projects.
 *
 * Executes the given [configuration] block against the [RepositoryHandler] for all sub-projects.
 *
 * @see Project.subprojects
 * @see Project.dependencies
 */
fun Project.subdependencies(configuration: DependencyHandlerToolkit.() -> Unit) = subprojects {
  afterEvaluate {
    DependencyHandlerToolkit(project, DependencyHandlerScope.of(dependencies)).configuration()
  }
}

/**
 * Changes the dependency version.
 *
 * For example, change to `1.2` version:
 * ```
 * "com.google:xyz:1.0" version "1.2"
 * ```
 *
 * @author 凛 (https://github.com/RinOrz)
 */
infix fun CharSequence.version(ver: String?): String = split(":").toMutableList().apply {
  removeLast()
  if (ver != null && ver.isNotEmpty()) add(ver)
}.joinToString(":")

/**
 * Removes the dependency version.
 */
fun withoutVersion(dependency: CharSequence): String = dependency version null

/**
 * Returns the group id from dependency.
 */
fun getGroupId(dependency: CharSequence): String = dependency.split(":")[0]

/**
 * Returns the artifact id from dependency.
 */
fun getArtifactId(dependency: CharSequence): String = dependency.split(":")[1]

/**
 * Returns the version from dependency.
 */
fun getVersion(dependency: CharSequence): String = dependency.split(":")[2]

/**
 * Removes the dependency version.
 */
@JvmName("removeVersion")
fun CharSequence.withoutVersion(): String = this version null

/**
 * Returns the group id from dependency.
 */
@JvmName("getDependencyGroup")
fun CharSequence.getGroupId(): String = this.split(":")[0]

/**
 * Returns the artifact id from dependency.
 */
@JvmName("getDependencyArtifact")
fun CharSequence.getArtifactId(): String = this.split(":")[1]

/**
 * Returns the version from dependency.
 */
@JvmName("getDependencyVersion")
fun CharSequence.getVersion(): String = this.split(":")[2]
