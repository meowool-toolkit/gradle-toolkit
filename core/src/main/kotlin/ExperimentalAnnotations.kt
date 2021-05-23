import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Use all given annotation classes [names] and suppress their experimental warning.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.useExperimentalAnnotations(vararg names: String) {
  addFreeCompilerArgs(names.map { "-Xopt-in=$it" })
  afterEvaluate {
    extensions.findByType<KotlinMultiplatformExtension>()?.sourceSets?.all {
      languageSettings {
        names.forEach(::useExperimentalAnnotation)
      }
    }
  }
}

/**
 * Use all given annotation classes [names] and suppress their experimental warning.
 */
fun Project.useExperimentalAnnotations(names: List<String>) =
  useExperimentalAnnotations(*names.toTypedArray())