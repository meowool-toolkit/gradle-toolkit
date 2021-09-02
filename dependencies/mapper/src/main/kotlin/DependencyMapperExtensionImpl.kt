@file:Suppress("EXPERIMENTAL_API_USAGE", "SpellCheckingInspection", "EXPERIMENTAL_IS_NOT_ENABLED",
  "BlockingMethodInNonBlockingContext")

import annotation.InternalGradleToolkitApi
import com.meowool.sweekt.coroutines.flowOnIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.gradle.api.Project
import java.io.File
import java.lang.System.currentTimeMillis
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * The mapper to produce the jar with mapped dependencies to class field members.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
@InternalGradleToolkitApi
class DependencyMapperExtensionImpl(project: Project) : DependencyMapperExtension(project) {
  @Volatile
  private var isMapping = false
  private val scriptModifier = GradleScriptModifier(project)
  private val pathSaved get() = cacheDir.resolve("path")
  private val cacheDir get() = project.buildDir.resolve("tmp/deps-mapping").apply { mkdirs() }
  private val cache get() = cacheDir.resolve("cache").apply {
    if (exists().not()) createNewFile()
  }
  private val outputFile get() = cacheDir.resolve("${currentTimeMillis()}.jar").also { output ->
    pathSaved.writeText(output.relativeTo(base = project.projectDir).path)
  }
  private val isUpdate get() = needUpdate?.invoke(project) ?: (cache.readText() != toString())

  @OptIn(ExperimentalTime::class)
  fun mapping() = runWithLock {
    if (isUpdate) {
      cacheDir.listFiles()?.forEach { if (it.extension == "jar") it.delete() }

      MappedClassesFactory.produce(rootClassName) {
        println("Mapping dependencies...")

        var mappingCount = mappedDependencies.size
        val consume = measureTime {
          // Dynamic
          channelFlow {
            sendAll { remoteDependencies?.fetch() }
            dependencies.forEach { send(Dependency(it)) }
          }.flowOnIO().collect {
            map(dependency = it, mappedClass = formatter.format(it))
            mappingCount++
          }

          // Specified
          mappedDependencies.forEach(::map)
        }

        println("Total $mappingCount dependencies are mapped, consume $consume.")
      }.toJar(outputFile)

      // Re-cache
      cache.writeText(this@DependencyMapperExtensionImpl.toString())
    }

    // Ensure classpath exists (relative)
    if (pathSaved.exists()) {
      scriptModifier.insertClasspathIfNotFound("classpath(files(\"${pathSaved.readText()}\"))")
    }
  }

  @Synchronized
  private fun runWithLock(block: suspend CoroutineScope.() -> Unit) = runBlocking(Dispatchers.IO) {
    if (isMapping) return@runBlocking
    isMapping = true
    block()
    isMapping = false
  }

  override fun toString(): String = buildString {
    val mappingDependencies = dependencies.joinToString("\n").prependIndent()
    if (mappingDependencies.isNotEmpty()) {
      appendLine("mappingDependencies:")
      appendLine(mappingDependencies)
    }
    val mappedDependencies = mappedDependencies.map { it.key + " -> " + it.value }.joinToString("\n").prependIndent()
    if (mappedDependencies.isNotEmpty()) {
      appendLine("mappedDependencies:")
      appendLine(mappedDependencies)
    }
    remoteDependencies?.apply {
      append("remoteDependencies.repositories: ")
      appendLine(defaultClientIds.joinToString())
      appendLine("remoteDependencies.keywords:")
      appendLine(keywords.joinToString("\n").prependIndent())
      appendLine("remoteDependencies.groups:")
      appendLine(groups.joinToString("\n").prependIndent())
      appendLine("remoteDependencies.startsWith:")
      appendLine(starts.joinToString("\n").prependIndent())
    }
  }
}