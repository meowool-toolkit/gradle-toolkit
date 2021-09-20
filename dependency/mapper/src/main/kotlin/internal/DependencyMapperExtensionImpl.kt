@file:Suppress("EXPERIMENTAL_API_USAGE", "NestedLambdaShadowedImplicitParameter")
@file:UseContextualSerialization(File::class)

package com.meowool.gradle.toolkit.internal

import com.meowool.gradle.toolkit.DependencyFormatter
import com.meowool.gradle.toolkit.DependencyMapperExtension
import com.meowool.gradle.toolkit.LibraryDependencyDeclaration
import com.meowool.gradle.toolkit.PluginDependencyDeclaration
import com.meowool.gradle.toolkit.ProjectDependencyDeclaration
import com.meowool.sweekt.HostingStack
import com.meowool.sweekt.datetime.nowMilliseconds
import com.meowool.sweekt.hosting
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
import kotlin.system.measureTimeMillis

/**
 * @author 凛 (https://github.com/RinOrz)
 */
internal class DependencyMapperExtensionImpl(private val project: Project) : DependencyMapperExtension {
  private val data: Data = Data()
  private val mutex: Mutex = Mutex()
  private val declarations: MutableMap<String, Any> = mutableMapOf()
  private var isUpdate: () -> Boolean = { cacheGraph.isInvalid }

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
  }

  fun mapping(): Boolean = runBlocking(Dispatchers.IO) {
    // Is remapping successful, otherwise use the cache
    var remapped = false

    cacheGraph = CacheGraph()
    mutex.withLock {
      // Merge jar paths cache
      data.jarsCache.merge(cacheGraph.cache?.jarsCache)

      if (isUpdate()) {
        project.logger.quiet("Mapping dependencies...")

        var mappingCount = 0
        val consume = measureTimeMillis {
          val jarPool = JarPool()

          consumeChannel {
            data.collectors.forEachConcurrently {
              with(it) {
                // Remap only when the corresponding cache is invalid
                if ((cacheGraph.isInvalidLibraries && it is LibraryDependencyDeclarationImpl.Data) ||
                  (cacheGraph.isInvalidProjects && it is ProjectDependencyDeclarationImpl.Data) ||
                  (cacheGraph.isInvalidPlugins && it is PluginDependencyDeclarationImpl.Data)
                ) collect(project, jarPool, data.formatter)
              }
            }
          }

          consumeChannel {
            fun writeEachJar(rootClassName: String, jar: Jar): File {
              mappingCount += jar.size()
              return cacheGraph.writeJar(rootClassName, jar)
            }

            if (cacheGraph.isInvalidLibraries) jarPool.apply {
              data.jarsCache.libraries.onEach { it.delete() }.clear()
              data.jarsCache.libraries += mapLibrariesJar(::writeEachJar)
            }

            if (cacheGraph.isInvalidProjects) jarPool.apply {
              data.jarsCache.projects.onEach { it.delete() }.clear()
              data.jarsCache.projects += mapProjectsJar(::writeEachJar)
            }

            if (cacheGraph.isInvalidPlugins) jarPool.apply {
              data.jarsCache.plugins.onEach { it.delete() }.clear()
              data.jarsCache.plugins += mapPluginsJar(::writeEachJar)
            }
          }
        }
        project.logger.quiet("Total $mappingCount dependencies are mapped, consume ${consume / 1000.0}s.")
        cacheGraph.writeCache()
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

    /////////////////////////////////////////////////////////////////////////
    ////    Invalidated when the any declared jar file does not exist    ////
    /////////////////////////////////////////////////////////////////////////

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

  companion object {
    const val CacheDir = ".dependency-mapper"
    const val CacheJson = "cache.json"
    const val CacheJarsDir = "jars"
  }
}