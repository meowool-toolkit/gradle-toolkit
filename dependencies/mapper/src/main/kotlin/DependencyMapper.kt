@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

import com.meowool.sweekt.coroutines.flowOnIO
import com.meowool.sweekt.removeFirst
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.extra
import java.io.File

/**
 * The mapper to produce the jar with mapped dependencies to class field members.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class DependencyMapper(project: Project) : DependencyMapperConfiguration(project) {
  private val isUpdate get() = needUpdate?.invoke(project) ?: (cacheFile.readText() != newCache)
  private val newCache get() = dependencies.joinToString() + mvnGroups.joinToString(prefix = ", ")
  private val cacheFile
    get() = project.buildDir.resolve("tmp/deps-mapping-cache").apply {
      parentFile.mkdirs()
      if (exists().not()) createNewFile()
    }

  private val output get() = outputFile ?: project.file(jarName)
  private val outputRelative
    get() = output.normalize().canonicalPath
      .removePrefix(project.rootDir.normalize().canonicalPath)
      .let { if (it[0] == '/' || it[0] == '\\') it.removeFirst() else it }

  private val rootProject get() = project.rootProject
  private val rootBuild: File?
    get() = rootProject.file("build.gradle.kts").takeIf { it.exists() }
      ?: rootProject.file("build.gradle").takeIf { it.exists() }
  private val rootSettings: File?
    get() = rootProject.file("settings.gradle.kts").takeIf { it.exists() }
      ?: rootProject.file("settings.gradle").takeIf { it.exists() }

  private val classpath get() = "classpath(files(\"$outputRelative\"))"

  private fun fetchMvnDependencies(): Flow<String> = when {
    mvnGroups.isEmpty() -> emptyFlow()
    else -> {
      val client = MvnRepositoryClient()
      mvnGroups
        .asFlow()
        .flatMapConcat {
          println("Fetch dependencies of `$it` group...")
          client.fetchArtifactDeps(it)
        }
        .onCompletion { client.close() }
        .flowOnIO()
    }
  }

  fun mapping() = runBlocking {
    if (isUpdate) {
      println("Mapping dependencies...")
      writeJar(rootClassName, output) { writer ->
        // Dynamic
        merge(dependencies.asFlow(), fetchMvnDependencies())
          .flattenToDepTree(formatter)
          .forEach(writer::add)

        // Specified
        mappingDependencies.forEach { (notation, mapped) ->
          val path = mapped.split('.')
          var innerWriter = writer
          path.forEachIndexed { index, name ->
            when (index) {
              path.lastIndex -> innerWriter.field(name, "$notation:_")
              else -> innerWriter = innerWriter.innerClass(name)
            }
          }
        }
      }

      // Re-cache
      cacheFile.writeText(newCache)
    }


    fun File.addBuildscript() = writeText(createBuildscriptBlock(if (exists()) readText() else "", classpath))
    fun File.insertClasspath() = writeText(insertClasspath(readText(), classpath))

    // Check buildscript
    when {
      // Select or create gradle script file
      rootBuild?.readText()?.hasBuildscriptBlock() == false ||
        rootSettings?.readText()?.hasBuildscriptBlock() == false -> rootSettings?.addBuildscript()
        ?: rootBuild?.addBuildscript()
        ?: rootProject.file("settings.gradle.kts").addBuildscript()

      rootBuild?.readText()?.contains(classpath).let { it != null && it.not() } -> rootBuild!!.insertClasspath()
      rootSettings?.readText()?.contains(classpath).let { it != null && it.not() } -> rootSettings!!.insertClasspath()
    }
  }
}

private const val KEY = "_dependencyMapper"

/**
 * Configures the dependency mapper based on the given [configuration].
 */
fun Project.dependencyMapper(configuration: DependencyMapperConfiguration.() -> Unit) = beforeEvaluate {
  when {
    extra.has(KEY) -> extra.get(KEY) as DependencyMapper
    else -> DependencyMapper(this).also { extra.set(KEY, it) }
  }.apply(configuration).mapping()
}

/**
 * Configures the dependency mapper based on the given [configuration].
 */
fun Settings.dependencyMapper(configuration: DependencyMapperConfiguration.() -> Unit) =
  gradle.rootProject { it.dependencyMapper(configuration) }