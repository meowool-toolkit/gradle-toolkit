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
 *
 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 */
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import java.io.File
import java.io.OutputStream
import java.util.Properties

internal class DependencyGenerateConfigurationImpl(
  private val project: Project
) : DependencyGenerateConfiguration() {
  var autoClose: Boolean = false
  var output: Any? = null

  private var upperCamelCase: (String) -> Boolean = { true }
  private var updatePredicate: (() -> Boolean)? = null
  private var dependencies: String? = null
  private var dependenciesSearch: String? = null
  private var replaceGroups: String? = null
  private var classNameTransformer: (String) -> String = { it }
  private var classParentTransformer: (String) -> String = { it }
  private val cacheFile by lazy {
    project.buildDir.resolve(".gd.config").apply {
      parentFile.mkdirs()
    }
  }

  fun getOutputData(): Any {
    if (output != null) return output!!

    // 默认输出到 buildSrc 中
    val buildSrc = project.rootDir.resolve("buildSrc")
    val outputDir = buildSrc.resolve("src/main/java")

    if (!outputDir.exists()) {
      outputDir.mkdirs()
    }

    if (!buildSrc.exists()) {
      buildSrc.resolve("build.gradle.kts").writeText(
        """
          repositories {
            mavenCentral()
            gradlePluginPortal()
          }

          plugins { `kotlin-dsl` }
        """.trimIndent()
      )
    }

    outputTo(outputDir.resolve("$fileName.kt"))
    return output!!
  }

  fun getDependencies(): List<Dependency> {
    requireNotNull(dependencies ?: dependenciesSearch) {
      "To generate dependency class, you must the declare dependencies by `declareDependencies` or `declareMavenCentralDependencies`."
    }
    val dependencies = dependencies.orEmpty() + "\n" + dependenciesSearch
      ?.let { DependenciesSearcher.search(it) }.orEmpty()

    return dependencies.apply { if (isBlank()) return emptyList() }
      .resolveDependencies(replaceGroups?.resolveDependencyGroupRules() ?: emptyMap())
      .onEach {
        it.standardization(
          simplifyClassName,
          upperCamelCase,
          classNameTransformer,
          classParentTransformer
        )
      }
  }

  fun isChanges(): Boolean {
    if ((output as? File)?.exists() == false) return true
    if ((output as? File)?.reader()?.readLines()?.isEmpty() == true) return true
    if (updatePredicate != null && updatePredicate?.invoke() == true) return true
    if (!cacheFile.exists()) return true
    return Properties().let {
      it.load(cacheFile.bufferedReader())
      val buildGradleUpdateTime = (it["build.gradle.update.time"] as? String)?.toLong()
        ?: return true
      val dependencies = it["dependencies"] as? String ?: return true
      val dependenciesSearch = it["dependenciesSearch"] as? String ?: return true
      val replaceGroups = it["replaceGroups"] as? String ?: return true
      // 修改时间超过 5 秒则更新
      (project.buildFile.lastModified() - buildGradleUpdateTime) > 5000 ||
        this.dependencies?.removeBlanks().toString() != dependencies ||
        this.dependenciesSearch?.removeBlanks().toString() != dependenciesSearch ||
        this.replaceGroups?.removeBlanks().toString() != replaceGroups
    }
  }

  fun cache() {
    cacheFile.writeText(
      """
        build.gradle.update.time=${project.buildFile.lastModified()}
        dependencies=${dependencies?.removeBlanks()}
        dependenciesSearch=${dependenciesSearch?.removeBlanks()}
        replaceGroups=${replaceGroups?.removeBlanks()}
      """.trimIndent()
    )
  }

  override fun upperCamelCase(predicate: (String) -> Boolean) {
    upperCamelCase = predicate
  }

  override fun declareDependencies(vararg declaration: String) {
    dependencies = declaration.joinToString("\n")
  }

  override fun declareDependencies(vararg declaration: File) {
    dependencies = declaration.joinToString("\n") { it.readText() }
  }

  override fun declareMavenCentralDependencies(vararg keywords: String) {
    dependenciesSearch = keywords.joinToString("\n")
  }

  override fun declareMavenCentralDependencies(vararg searchDeclaration: File) {
    dependenciesSearch = searchDeclaration.joinToString("\n") { it.readText() }
  }

  override fun replaceGroups(vararg rule: String) {
    replaceGroups = rule.joinToString("\n")
  }

  override fun replaceGroups(vararg rule: File) {
    replaceGroups = rule.joinToString("\n") { it.readText() }
  }

  override fun classNameTransform(transformation: (String) -> String) {
    classNameTransformer = transformation
  }

  override fun classParentTransform(transformation: (String) -> String) {
    classParentTransformer = transformation
  }

  override fun outputTo(appendable: Appendable) {
    output = appendable
  }

  override fun outputTo(outputStream: OutputStream) {
    output = outputStream.bufferedWriter()
    autoClose = true
  }

  override fun outputTo(outputFile: File) {
    outputFile.parentFile.mkdirs()
    if (!outputFile.exists()) alwaysUpdate()
    this.output = outputFile
    autoClose = true
  }

  override fun outputToProject() {
    val dir = project.convention.findPlugin(JavaPluginConvention::class.java)
      ?.sourceSets?.findByName("main")
      ?.java?.srcDirs?.first() ?: error("Cannot find the sources code folder of current project, please use `outputToDirectory`.")
    outputToDirectory(dir)
  }

  override fun outputToDirectory(outputDir: File) {
    outputTo(outputDir.resolve("$fileName.kt"))
  }

  override fun alwaysUpdate() {
    updatePredicate = { true }
  }

  override fun updateWhen(predicate: () -> Boolean) {
    updatePredicate = predicate
  }
}
