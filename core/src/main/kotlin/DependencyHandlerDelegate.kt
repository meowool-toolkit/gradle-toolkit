import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.gradleKotlinDsl

/**
 * Delegate the dependencies of [project].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class DependencyHandlerDelegate(val project: Project, delegate: DependencyHandler) : DependencyHandler by delegate {

  /**
   * Creates a dependency on the API of the current version of the Gradle Kotlin DSL.
   *
   * Includes the Kotlin and Gradle APIs.
   *
   * @see Project.gradleKotlinDsl
   */
  fun gradleKotlinDsl(): Dependency = project.gradleKotlinDsl()
}