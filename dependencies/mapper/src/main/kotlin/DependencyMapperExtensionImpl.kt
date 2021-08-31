@file:Suppress("EXPERIMENTAL_API_USAGE", "SpellCheckingInspection", "EXPERIMENTAL_IS_NOT_ENABLED",
  "BlockingMethodInNonBlockingContext")

import com.meowool.sweekt.coroutines.flowOnIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.runBlocking
import org.gradle.api.Project
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * The mapper to produce the jar with mapped dependencies to class field members.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class DependencyMapperExtensionImpl(project: Project) : DependencyMapperExtension(project) {
  @Volatile
  private var isMapping = false
  private val scriptModifier = GradleScriptModifier(project)
  private val outputPath get() = outputFile.relativeTo(base = project.rootDir).normalize().path
  private val isUpdate get() = needUpdate?.invoke(project) ?: (cacheFile.readText() != toString())
  private val cacheFile get() = project.buildDir.resolve("tmp/deps-mapping-cache").apply {
    parentFile.mkdirs()
    if (exists().not()) createNewFile()
  }

  @OptIn(ExperimentalTime::class)
  fun mapping() = runWithLock {
    if (isUpdate) {
      MappedClassesFactory.produce(rootClassName) {
        println("Mapping dependencies...")

        var mappingCount = mappedDependencies.size
        val consume = measureTime {
          // Specified
          mappedDependencies.forEachConcurrently { (dependency, mappedClass) ->
            map(dependency, mappedClass)
          }

          // Dynamic
          channelFlow {
            sendAll { remoteDependencies?.fetch() }
            dependencies.forEachConcurrently { send(Dependency(it)) }
          }.flowOnIO().collectConcurrently {
            map(dependency = it, mappedClass = formatter.format(it))
            mappingCount++
          }
        }

        println("Total $mappingCount dependencies are mapped, consume $consume.")
      }.toJar(outputFile)

      // Re-cache
      cacheFile.writeText(this@DependencyMapperExtensionImpl.toString())
    }

    // Ensure classpath exists
    scriptModifier.insertClasspathIfNotFound("classpath(files(\"$outputPath\"))")
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