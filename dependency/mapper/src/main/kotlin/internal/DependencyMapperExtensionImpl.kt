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
@file:Suppress("EXPERIMENTAL_API_USAGE", "NestedLambdaShadowedImplicitParameter")
@file:UseContextualSerialization(File::class)

package com.meowool.gradle.toolkit.internal

import com.meowool.gradle.toolkit.DependencyFormatter
import com.meowool.gradle.toolkit.DependencyMapperExtension
import com.meowool.gradle.toolkit.LibraryDependencyDeclaration
import com.meowool.gradle.toolkit.PluginDependencyDeclaration
import com.meowool.gradle.toolkit.ProjectDependencyDeclaration
import com.meowool.gradle.toolkit.internal.DependencyMapperInternal.CacheDir
import com.meowool.gradle.toolkit.internal.DependencyMapperInternal.CacheJarsDir
import com.meowool.gradle.toolkit.internal.DependencyMapperInternal.CacheJson
import com.meowool.sweekt.datetime.nowMilliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseContextualSerialization
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.gradle.api.Project
import org.gradle.kotlin.dsl.buildscript
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.measureTime

/**
 * @author 凛 (https://github.com/RinOrz)
 */
@PublishedApi
internal class DependencyMapperExtensionImpl(override val project: Project) : DependencyMapperExtension {
  private val data: Data = Data()
  private val mappingMutex: Mutex = Mutex()
  private val collectMutex: Mutex = Mutex()
  private val declarations: MutableMap<String, Any> = mutableMapOf()
  private var isUpdate: () -> Boolean = { cacheGraph.isInvalid }
  private var isConcurrently: Boolean = true

  private lateinit var cacheGraph: CacheGraph

  override fun libraries(
    rootClassName: String,
    configuration: LibraryDependencyDeclaration.() -> Unit,
  ): LibraryDependencyDeclaration {
    val declaration = declarations.getOrPut(rootClassName) {
      LibraryDependencyDeclarationImpl(rootClassName).also { data.collectors += it.data }
    }
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
    val declaration = declarations.getOrPut(rootClassName) {
      ProjectDependencyDeclarationImpl(rootClassName, project).also { data.collectors += it.data }
    }
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
    val declaration = declarations.getOrPut(rootClassName) {
      PluginDependencyDeclarationImpl(rootClassName).also { data.collectors += it.data }
    }
    require(declaration is PluginDependencyDeclarationImpl) {
      "$rootClassName already exists，you cannot use duplicate name as the root class name of plugins mapping."
    }
    declaration.apply(configuration)
    return declaration
  }

  override fun format(formatter: DependencyFormatter.() -> Unit) = data.formatter.run(formatter)

  override fun concurrency(isConcurrently: Boolean) {
    this.isConcurrently = isConcurrently
  }

  override fun updateWhen(predicate: (Project) -> Boolean) {
    isUpdate = { predicate(project) }
  }

  override fun alwaysUpdate() {
    isUpdate = { true }
  }

  private fun closeAllClients() {
    DependencyRepository.MavenCentral.closeClient()
    DependencyRepository.Google.closeClient()
    DependencyRepository.GradlePluginPortal.closeClient()
    DependencyRepository.MvnRepository.closeClient()
    DependencyRepository.MvnExactlyRepository.closeClient()
  }

  fun collectDependencies(destination: File) = runBlocking(Dispatchers.IO) {
    collectMutex.withLock {
      val outputList = DependencyMapperInternal.DependencyOutputList()
      val consume = measureTime {
//        when {
//          isConcurrently -> data.collectors.forEach {
//
//          }
//        }
        consumeChannel(isConcurrently) {
          data.collectors.forEachConcurrently {
            with(it) { collect(project, isConcurrently, outputList) }
          }
        }
      }
      // Output to list and write to file (used to CI)
      destination.writeText(DefaultJson.encodeToString(outputList))
      project.logger.quiet("A total of ${outputList.size} dependencies are output, consume $consume.")
    }
  }

  fun mapping(): Boolean = runBlocking(Dispatchers.IO) {
    // Is remapping successful, otherwise use the cache
    var remapped = false

    cacheGraph = CacheGraph()
    mappingMutex.withLock {
      // Merge jar paths cache
      data.jarsCache.merge(cacheGraph.cache?.jarsCache)

      if (isUpdate()) {
        project.logger.quiet("Mapping dependencies...")

        val mappingCount = AtomicInteger()
        val consume = measureTime {
          val jarPool = JarPool()

          consumeChannel(isConcurrently) {
            data.collectors.forEachConcurrently {
              with(it) {
                // Remap only when the corresponding cache is invalid
                if ((cacheGraph.isInvalidLibraries && it is LibraryDependencyDeclarationImpl.Data) ||
                  (cacheGraph.isInvalidProjects && it is ProjectDependencyDeclarationImpl.Data) ||
                  (cacheGraph.isInvalidPlugins && it is PluginDependencyDeclarationImpl.Data)
                ) collect(project, jarPool, isConcurrently, data.formatter)
              }
            }
          }

          consumeChannel(isConcurrently) {
            fun writeEachJar(rootClassName: String, jar: Jar): File {
              mappingCount.addAndGet(jar.size())
              return cacheGraph.writeJar(rootClassName, jar)
            }

            if (cacheGraph.isInvalidLibraries) jarPool.apply {
              data.jarsCache.libraries.onEach { it.delete() }.clear()
              data.jarsCache.libraries += libraries.mapConcurrently(::writeEachJar)
            }

            if (cacheGraph.isInvalidProjects) jarPool.apply {
              data.jarsCache.projects.onEach { it.delete() }.clear()
              data.jarsCache.projects += projects.mapConcurrently(::writeEachJar)
            }

            if (cacheGraph.isInvalidPlugins) jarPool.apply {
              data.jarsCache.plugins.onEach { it.delete() }.clear()
              data.jarsCache.plugins += plugins.mapConcurrently(::writeEachJar)
            }

            cacheGraph.writeCache()
          }
        }

        project.logger.quiet("A total of ${mappingCount.get()} dependencies are mapped, consume $consume.")
        closeAllClients()
        remapped = true
      }

      project.rootProject.buildscript {
        dependencies.classpath(project.files(data.jarsCache.all))
      }
    }

    remapped
  }

  @Serializable
  internal data class Data(
    /**
     * @see LibraryDependencyDeclarationImpl.Data
     * @see ProjectDependencyDeclarationImpl.Data
     * @see PluginDependencyDeclarationImpl.Data
     */
    val collectors: MutableList<DependencyCollector> = mutableListOf(),
    val formatter: DependencyFormatter = DependencyFormatter(),
    val jarsCache: JarsCache = JarsCache(),
  )

  /**
   * Cache paths of each type of jar.
   *
   * ```
   * archivesDir
   *
   * - Libs-00000.jar
   *   Libs2-00000.jar
   *   ---
   *   Projects-00000.jar
   *   ProjectPaths-00000.jar
   *   ---
   *   Plugins-00000.jar
   * ```
   */
  @Serializable
  internal data class JarsCache(
    val libraries: MutableList<File> = mutableListOf(),
    val projects: MutableList<File> = mutableListOf(),
    val plugins: MutableList<File> = mutableListOf(),
  ) {

    val all: List<File> get() = libraries + projects + plugins

    // ///////////////////////////////////////////////////////////////////////
    // //    Invalidated when the any declared jar file does not exist    ////
    // ///////////////////////////////////////////////////////////////////////

    val isInvalidLibraries: Boolean get() = libraries.any { it.exists().not() }
    val isInvalidProjects: Boolean get() = projects.any { it.exists().not() }
    val isInvalidPlugins: Boolean get() = plugins.any { it.exists().not() }

    fun merge(other: JarsCache?) {
      if (other == null) return
      libraries += other.libraries
      projects += other.projects
      plugins += other.plugins
    }
  }

  private inner class CacheGraph {
    private val dir: File get() = project.projectDir.resolve(CacheDir).apply { mkdirs() }
    private val json: File get() = dir.resolve(CacheJson)
    private val jarsDir: File get() = dir.resolve(CacheJarsDir).apply { mkdirs() }

    val cache: Data? by lazy {
      when {
        json.exists() -> DefaultJson.decodeFromString<Data>(json.readText())
        else -> null
      }
    }

    val isInvalid: Boolean
      get() = cache == null || cache?.formatter != data.formatter ||
        isInvalidLibraries || isInvalidProjects || isInvalidPlugins

    val isInvalidLibraries: Boolean
      // Invalidated when the declaration is different from the cache
      get() = cache?.jarsCache?.isInvalidLibraries == true ||
        cache?.collectors?.filterIsInstance<LibraryDependencyDeclarationImpl.Data>() !=
        data.collectors.filterIsInstance<LibraryDependencyDeclarationImpl.Data>()

    val isInvalidProjects: Boolean
      // Invalidated when the declaration is different from the cache
      get() = cache?.jarsCache?.isInvalidProjects == true ||
        cache?.collectors?.filterIsInstance<ProjectDependencyDeclarationImpl.Data>() !=
        data.collectors.filterIsInstance<ProjectDependencyDeclarationImpl.Data>()

    val isInvalidPlugins: Boolean
      // Invalidated when the declaration is different from the cache
      get() = cache?.jarsCache?.isInvalidPlugins == true ||
        cache?.collectors?.filterIsInstance<PluginDependencyDeclarationImpl.Data>() !=
        data.collectors.filterIsInstance<PluginDependencyDeclarationImpl.Data>()

    fun writeJar(name: String, jar: Jar): File = jarsDir.resolve("$name-$nowMilliseconds.jar").also {
      jar.make().toJar(it)
    }

    fun writeCache() {
      json.writeText(DefaultJson.encodeToString(data))
    }
  }
}
