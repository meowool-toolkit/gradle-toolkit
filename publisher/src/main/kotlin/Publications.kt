import com.meowool.gradle.toolkit.GradleToolkitExtension
import com.meowool.gradle.toolkit.publisher.PublicationExtension
import com.meowool.gradle.toolkit.publisher.PublisherPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

/**
 * Configures the [PublicationExtension] of the [PublisherPlugin] plugin in all projects.
 *
 * @param configuration Configure publication extension.
 *
 * @see PublisherPlugin
 * @see PublicationExtension
 */
fun GradleToolkitExtension.publications(configuration: PublicationExtension.() -> Unit) = allprojects {
  apply<PublisherPlugin>()
  extensions.configure(configuration)
}

/**
 * Returns the [PublicationExtension] of the [PublisherPlugin] plugin in this project.
 *
 * @see PublisherPlugin
 * @see PublicationExtension
 */
val Project.publication: PublicationExtension get() {
  apply<PublisherPlugin>()
  return extensions.getByType()
}

/**
 * Configures the [PublicationExtension] of the [PublisherPlugin] plugin in this project.
 *
 * @param configuration Configure publication extension.
 *
 * @see PublisherPlugin
 * @see PublicationExtension
 */
fun Project.publication(configuration: PublicationExtension.() -> Unit) {
  apply<PublisherPlugin>()
  extensions.configure(configuration)
}