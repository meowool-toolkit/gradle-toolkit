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
package extension

import ApplyProjectScope
import ApplySourceScope
import MainScope
import addFreeCompilerArgs
import annotation.InternalGradleDslXApi
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

internal open class GradleDslExtensionImpl(override val project: Project) : GradleDslExtension {

  @InternalGradleDslXApi
  override val data: MutableList<Any> = mutableListOf()

  override var scope: String? = null

  override fun configureKotlinCompile(configuration: KotlinCompile.() -> Unit) {
    project.allprojects {
      tasks.withType<KotlinCompile> {
        if (name == "compileKotlin") configuration()
      }
    }
  }

  override fun configureKotlinTestCompile(configuration: KotlinCompile.() -> Unit) {
    project.allprojects {
      tasks.withType<KotlinCompile> {
        if (name == "compileTestKotlin") configuration()
      }
    }
  }

  override fun configureAllKotlinCompile(configuration: KotlinCompile.() -> Unit) {
    configureKotlinCompile(configuration)
    configureKotlinTestCompile(configuration)
  }

  override fun kotlinExplicitApi(mode: ExplicitApiMode, applyScope: ApplySourceScope) {
    when (applyScope) {
      ApplySourceScope.All -> configureAllKotlinCompile {
        kotlinOptions {
          addFreeCompilerArgs("-Xexplicit-api=${mode.name}")
        }
      }
      else -> {
        configureKotlinCompile {
          kotlinOptions {
            val name = if (applyScope == ApplySourceScope.Default) mode.name else ExplicitApiMode.Disabled.name
            addFreeCompilerArgs("-Xexplicit-api=$name")
          }
        }
        configureKotlinTestCompile {
          kotlinOptions {
            val name = if (applyScope == ApplySourceScope.Test) mode.name else ExplicitApiMode.Disabled.name
            addFreeCompilerArgs("-Xexplicit-api=$name")
          }
        }
      }
    }
  }
}

internal class RootGradleDslExtensionImpl(override val rootProject: Project) :
  GradleDslExtensionImpl(rootProject), RootGradleDslExtension {
  val sharedDependencies = hashMapOf<String, DependencyHandler.() -> Unit>()
  val sharedLazyDependencies = hashMapOf<String, DependencyHandler.() -> Unit>()
  override val allprojects: Set<Project>
    get() = rootProject.allprojects
  override val subprojects: Set<Project>
    get() = rootProject.subprojects

  override fun allprojects(action: Project.() -> Unit) = rootProject.allprojects(action)
  override fun subprojects(action: Project.() -> Unit) = rootProject.subprojects(action)

  override fun addMaven(
    url: Any,
    applyScope: ApplyProjectScope,
    action: MavenArtifactRepository.() -> Unit
  ) = resolveApply(applyScope) { repositories.maven(url, action) }

  override fun addMavenCentral(args: Map<String, Any?>?, applyScope: ApplyProjectScope) =
    resolveApply(applyScope) { args?.let(repositories::mavenCentral) ?: repositories.mavenCentral() }

  override fun addMavenLocal(
    applyScope: ApplyProjectScope,
    action: MavenArtifactRepository.() -> Unit
  ) = resolveApply(applyScope) { repositories.mavenLocal(action) }

  override fun addFlatDir(args: Map<String, Any?>, applyScope: ApplyProjectScope) {
    resolveApply(applyScope) { repositories.flatDir(args) }
  }

  override fun addGradlePluginPortal(
    applyScope: ApplyProjectScope,
    action: ArtifactRepository.() -> Unit
  ) = resolveApply(applyScope) { repositories.gradlePluginPortal(action) }

  override fun addGoogle(
    applyScope: ApplyProjectScope,
    action: MavenArtifactRepository.() -> Unit
  ) = resolveApply(applyScope) { repositories.google(action) }

  override fun shareDependencies(scope: String?, block: DependencyHandler.() -> Unit) {
    sharedDependencies[scope ?: MainScope] = block
  }

  override fun shareLazyDependencies(scope: String?, block: DependencyHandler.() -> Unit) {
    sharedLazyDependencies[scope ?: MainScope] = block
  }

  private fun resolveApply(applyScope: ApplyProjectScope, block: Project.() -> Unit) {
    when (applyScope) {
      ApplyProjectScope.CurrentProject -> rootProject.block()
      ApplyProjectScope.SubProjects -> rootProject.subprojects { block() }
      ApplyProjectScope.AllProjects -> rootProject.allprojects { block() }
    }
  }
}
