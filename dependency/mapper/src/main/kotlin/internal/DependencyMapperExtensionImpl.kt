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
 * 除如果您正在修改此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
@file:Suppress("BlockingMethodInNonBlockingContext", "EXPERIMENTAL_API_USAGE")

package com.meowool.gradle.toolkit.internal

import com.meowool.gradle.toolkit.DependencyFormatter
import com.meowool.gradle.toolkit.DependencyMapperExtension
import com.meowool.gradle.toolkit.LibraryDependencyDeclaration
import com.meowool.gradle.toolkit.PluginDependencyDeclaration
import com.meowool.gradle.toolkit.ProjectDependencyDeclaration
import com.meowool.sweekt.coroutines.flowOnIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.gradle.api.Project
import org.gradle.kotlin.dsl.buildscript
import java.io.File
import java.lang.System.currentTimeMillis
import kotlin.system.measureTimeMillis

/**
 * @author 凛 (https://github.com/RinOrz)
 */
@Serializable @InternalGradleToolkitApi
class DependencyMapperExtensionImpl(
  @Transient
  private val project: Project? = null
) : DependencyMapperExtension {
  @Transient @Volatile
  private var isMapping = false
  private val formatter = DependencyFormatter()
  internal val declarations = mutableMapOf<String, MapDeclaration>()

  @Transient @Volatile
  private var onlyPathChanges: Boolean = false

  /** The default is only when the [Project.cacheJson] is changed. */
  @Transient
  private var isUpdate = fun(project: Project): Boolean {
    // Update if jar path cache or jar does not exist
    if (project.cacheJar.exists().not() || File(project.cacheJar.readText()).exists().not()) return true
    if (project.cacheJson.exists().not() || project.cacheJson.readText().isEmpty()) return true
    if (toJson() != project.cacheJson.readText()) return true

    // Project mapping is enabled, compare whether the projects in the cache need to be updated
    if (declarations.filterValues { it is ProjectDependencyDeclarationImpl }.isNotEmpty()) {
      if (
        project.cacheProjects.exists().not() ||
        DefaultJson.decodeFromString<List<String>>(project.cacheProjects.readText()) != subprojectPaths()
      ) {
        onlyPathChanges = true
        return true
      }
    }
    return false
  }

  override var jarPrefix: String = "classes"

  override fun libraries(
    rootClassName: String,
    configuration: LibraryDependencyDeclaration.() -> Unit,
  ): LibraryDependencyDeclaration {
    val declaration = declarations.getOrPut(rootClassName) { LibraryDependencyDeclarationImpl(rootClassName) }
    require(declaration is LibraryDependencyDeclarationImpl) {
      "$rootClassName already exists，you cannot use duplicate name as the root class name of libraries mapping."
    }
    declaration.apply(configuration)
    return declaration
  }

  override fun projects(
    rootClassName: String,
    configuration: ProjectDependencyDeclaration.() -> Unit,
  ): ProjectDependencyDeclaration {
    val declaration = declarations.getOrPut(rootClassName) { ProjectDependencyDeclarationImpl(rootClassName, project) }
    require(declaration is ProjectDependencyDeclarationImpl) {
      "$rootClassName already exists，you cannot use duplicate name as the root class name of projects mapping."
    }
    declaration.apply(configuration)
    return declaration
  }

  override fun plugins(
    rootClassName: String,
    configuration: PluginDependencyDeclaration.() -> Unit,
  ): PluginDependencyDeclaration {
    val declaration = declarations.getOrPut(rootClassName) { PluginDependencyDeclarationImpl(rootClassName) }
    require(declaration is PluginDependencyDeclarationImpl) {
      "$rootClassName already exists，you cannot use duplicate name as the root class name of plugins mapping."
    }
    declaration.apply(configuration)
    return declaration
  }

  override fun format(formatter: DependencyFormatter.() -> Unit) = this.formatter.run(formatter)

  override fun updateWhen(predicate: (Project) -> Boolean) {
    isUpdate = predicate
  }

  override fun alwaysUpdate() {
    isUpdate = { true }
  }

  /**
   * TODO:
   *   1. Optimize to avoid all updates
   *   3. Compatible buildSrc
   *   2. Use measureTime instead of measureTimeMillis when Gradle support kotlin 1.5.30
   */
  fun mapping() = runWithLock {
    var cacheJar = project!!.cacheJar.takeIf { it.exists() }?.let { File(it.readText()) }

    if (isUpdate(project)) {
      val newCacheJar = project.cacheDir.resolve(jarPrefix + '-' + currentTimeMillis() + ".jar")

      if (onlyPathChanges.not() || cacheJar == null) {
        // Map to a new jar (prevent gradle caching)
        cacheJar?.delete()
        cacheJar = newCacheJar
        cacheJar.createNewFile()
      } else {
        cacheJar.copyTo(newCacheJar, overwrite = true)
        cacheJar.delete()
        cacheJar = newCacheJar
      }

      // Cache the new jar file path
      project.cacheJar.writeText(cacheJar.absolutePath)

      println("Mapping dependencies...")

      var mappingCount = 0
      val consume = measureTimeMillis {
        val declarations = when {
          onlyPathChanges -> declarations.values.filterIsInstance<ProjectDependencyDeclarationImpl>()
          else -> declarations.values
        }
        val factories = mutableMapOf<String, MappedClassesFactory>()
        channelFlow {
          declarations.forEachConcurrently { declaration ->
            val factory = factories.getOrPut(declaration.rootClassName) {
              MappedClassesFactory(declaration.rootClassName)
            }
            send(
              declaration
                .toFlow(this@DependencyMapperExtensionImpl, formatter)
                .flowOnIO()
                .collect {
                  factory.map(it.dependency, it.mappedPath)
                  mappingCount++
                }
            )
          }
        }.onCompletion {
          declarations.filterIsInstance<PluginDependencyDeclarationImpl>().forEach { declaration ->
            val factory = factories[declaration.rootClassName]
            val forgotten = declaration.getForgottenDependencies(formatter)
            forgotten.forEach { factory?.map(it.dependency, it.mappedPath) }
            mappingCount += forgotten.size
          }
        }.collect()

        factories.values.forEach { it.make().inject(cacheJar) }
      }
      println("Total $mappingCount dependencies are mapped, consume ${consume / 1000.0}s.")

      updateCache()
    }

    project.rootProject.buildscript {
      dependencies.classpath(project.files(cacheJar))
    }
  }

  @Synchronized
  private fun runWithLock(block: suspend CoroutineScope.() -> Unit) = runBlocking(Dispatchers.IO) {
    if (isMapping) return@runBlocking
    isMapping = true
    block()
    isMapping = false
  }

  private fun updateCache() {
    project!!.cacheJson.writeText(toJson())
    project.cacheProjects.writeText(subprojectPaths().toJson())
  }
  private fun subprojectPaths() = project!!.subprojects.map { it.path }
  inline fun <reified T> T.toJson() = DefaultJson.encodeToString(this)

  companion object {
    val Project.cacheDir get() = projectDir.resolve(".dependency-mapper").apply { mkdirs() }
    val Project.cacheJson get() = cacheDir.resolve("cache.json")
    val Project.cacheProjects get() = cacheDir.resolve("cache-projects.json")
    val Project.cacheJar get() = cacheDir.resolve("jar.path")
  }
}
