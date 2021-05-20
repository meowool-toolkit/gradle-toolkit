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
@file:Suppress("SpellCheckingInspection")

package extension

import ApplyProjectScope
import MavenMirrors
import importLazyDependencies
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository

/**
 * Contains all root extensions for gradle-dsl-x.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
interface RootGradleDslExtension : GradleDslExtension {

  /**
   * Returns the root project.
   */
  val rootProject: Project

  /**
   * Returns the all projects of root project.
   *
   * @see Project.getAllprojects
   */
  val allprojects: Set<Project>

  /**
   * Returns the sub-projects of root project.
   *
   * @see Project.getSubprojects
   */
  val subprojects: Set<Project>

  /**
   * Execute [action] of each all projects.
   *
   * @see Project.allprojects
   */
  fun allprojects(action: Project.() -> Unit)

  /**
   * Execute [action] of each sub projects.
   *
   * @see Project.subprojects
   */
  fun subprojects(action: Project.() -> Unit)

  /**
   * Set up dependencies maven repository for [applyScope] projects.
   *
   * @see RepositoryHandler.maven
   */
  fun addMaven(
    url: Any,
    applyScope: ApplyProjectScope = ApplyProjectScope.AllProjects,
    action: MavenArtifactRepository.() -> Unit = {}
  )

  /**
   * Set up dependencies [mirror] repository for [applyScope] projects.
   */
  fun addMavenMirror(
    mirror: MavenMirrors,
    applyScope: ApplyProjectScope = ApplyProjectScope.AllProjects,
    action: MavenArtifactRepository.() -> Unit = {},
  ) {
    addMaven(mirror.url, applyScope, action)
  }

  /** @see RepositoryHandler.mavenCentral */
  fun addMavenCentral(
    args: Map<String, Any?>? = null,
    applyScope: ApplyProjectScope = ApplyProjectScope.AllProjects,
  )

  /** @see RepositoryHandler.mavenCentral */
  fun addMavenSnapshots(
    applyScope: ApplyProjectScope = ApplyProjectScope.AllProjects,
    action: MavenArtifactRepository.() -> Unit = {},
  ) {
    addMaven("https://oss.sonatype.org/content/repositories/snapshots", applyScope, action)
  }

  /** @see RepositoryHandler.mavenCentral */
  fun addMavenS01Snapshots(
    applyScope: ApplyProjectScope = ApplyProjectScope.AllProjects,
    action: MavenArtifactRepository.() -> Unit = {},
  ) {
    addMaven("https://s01.oss.sonatype.org/content/repositories/snapshots", applyScope, action)
  }

  /** @see RepositoryHandler.mavenLocal */
  fun addMavenLocal(
    applyScope: ApplyProjectScope = ApplyProjectScope.AllProjects,
    action: MavenArtifactRepository.() -> Unit = {},
  )

  /** @see RepositoryHandler.flatDir */
  fun addFlatDirRepo(
    args: Map<String, Any?>,
    applyScope: ApplyProjectScope = ApplyProjectScope.AllProjects,
  )

  /** @see RepositoryHandler.gradlePluginPortal */
  fun addGradlePluginPortal(
    applyScope: ApplyProjectScope = ApplyProjectScope.AllProjects,
    action: ArtifactRepository.() -> Unit = {}
  )

  /** @see RepositoryHandler.google */
  fun addGoogleRepo(
    applyScope: ApplyProjectScope = ApplyProjectScope.AllProjects,
    action: MavenArtifactRepository.() -> Unit = {},
  )

  /** @see RepositoryHandler.jcenter */
  @Deprecated(
    message = "JCenter sunset on 2020-05-01, it is recommended to mavenCentral, but you can also use mirrors.",
    replaceWith = ReplaceWith("addMavenMirror(MavenMirrors.Aliyun.JCenter)")
  ) fun addJCenter(
    applyScope: ApplyProjectScope = ApplyProjectScope.AllProjects,
    action: MavenArtifactRepository.() -> Unit = {}
  ) {
    addMavenMirror(MavenMirrors.Aliyun.JCenter)
  }

  fun addKotlinEapRepo(
    applyScope: ApplyProjectScope = ApplyProjectScope.AllProjects,
    action: MavenArtifactRepository.() -> Unit = {}
  ) {
    addMaven("https://dl.bintray.com/kotlin/kotlin-eap", applyScope, action)
  }

  fun addJitpack(
    applyScope: ApplyProjectScope = ApplyProjectScope.AllProjects,
    action: MavenArtifactRepository.() -> Unit = {}
  ) {
    addMaven("https://jitpack.io", applyScope, action)
  }

  /**
   * Share a reusable dependencies code [block].
   *
   * When the project matches the [scope],
   * the shared dependencies are automatically imported.
   *
   * NOTE: Very useful when having the same dependencies in multi-modules,
   * this can greatly improve code conciseness.
   *
   * ```
   * shareDependencies {
   *   // define common dependencies.
   *   implementationOf(
   *     Kotlin.stdlib.jdk8,
   *     AndroidX.appCompat,
   *     ...
   *   )
   * }
   * shareDependencies("1") {
   *   // define scope-1 dependencies.
   *   implementationOf(
   *     KotlinX.reflect.lite,
   *     KotlinX.coroutines.jdk8,
   *   )
   * }
   * ```
   * @see Project.dependencies
   * @see GradleDslExtension.scope
   * @param scope representative the effect scope of this dependencies shared block.
   */
  fun shareDependencies(
    scope: String? = null,
    block: DependencyHandler.() -> Unit
  )

  /**
   * Share a reusable dependencies code [block].
   *
   * Unlike [shareDependencies], you must manually call [importLazyDependencies] to
   * import dependencies.
   *
   * NOTE: Very useful when having the same dependencies in multi-modules, this can greatly
   * improve code conciseness.
   *
   * ```
   * shareLazyDependencies {
   *   // define common lazy dependencies.
   *   implementationOf(
   *     Kotlin.stdlib.jdk8,
   *     AndroidX.appCompat,
   *     ...
   *   )
   * }
   * shareLazyDependencies("1") {
   *   // define scope-1 lazy dependencies.
   *   implementationOf(
   *     KotlinX.reflect.lite,
   *     KotlinX.coroutines.jdk8,
   *   )
   * }
   * ```
   * @see Project.dependencies
   * @see GradleDslExtension.scope
   * @param scope representative the effect scope of this dependencies shared block.
   */
  fun shareLazyDependencies(
    scope: String? = null,
    block: DependencyHandler.() -> Unit
  )

  @Deprecated("unsupported", level = DeprecationLevel.HIDDEN)
  override var scope: String?
}
