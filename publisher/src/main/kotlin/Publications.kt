import com.meowool.toolkit.gradle.PublicationExtension
import com.meowool.toolkit.gradle.Publisher
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

/**
 * Configures the [PublicationExtension] of the [Publisher] plugin in all projects.
 *
 * @param configuration Configure publication extension.
 *
 * @see Publisher
 * @see PublicationExtension
 */
fun GradleToolkitExtension.publications(configuration: PublicationExtension.() -> Unit) = allprojects {
  apply<Publisher>()
  extensions.configure(configuration)
}

/**
 * Returns the [PublicationExtension] of the [Publisher] plugin in this project.
 *
 * @see Publisher
 * @see PublicationExtension
 */
val Project.publication: PublicationExtension get() {
  apply<Publisher>()
  return extensions.getByType()
}

/**
 * Configures the [PublicationExtension] of the [Publisher] plugin in this project.
 *
 * @param configuration Configure publication extension.
 *
 * @see Publisher
 * @see PublicationExtension
 */
fun Project.publication(configuration: PublicationExtension.() -> Unit) {
  apply<Publisher>()
  afterEvaluate { extensions.configure(configuration) }
}