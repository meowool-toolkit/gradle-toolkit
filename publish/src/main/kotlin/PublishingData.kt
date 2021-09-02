@file:Suppress("MemberVisibilityCanBePrivate")

import PublishingData.Companion.publishingDataExtensions
import annotation.InternalGradleToolkitApi
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPomDeveloper
import org.gradle.api.publish.maven.MavenPomLicense
import org.gradle.api.publish.maven.MavenPomOrganization
import org.gradle.api.publish.maven.MavenPomScm
import org.gradle.kotlin.dsl.get

@DslMarker annotation class PublishingDataMarker

/**
 * A lazy publishing data class for the Maven publication.
 *
 * For more details, see:
 * [Gradle Document](https://docs.gradle.org/current/userguide/publishing_maven.html#sec:modifying_the_generated_pom)
 * [Maven Document](https://maven.apache.org/pom.html)
 *
 * @author 凛 (https://github.com/RinOrz)
 * @see MavenPom for more details.
 */
@PublishingDataMarker
class PublishingData(private val project: Project) {

  /**
   * The group id of a Maven publication.
   *
   * By default, it is searched through the `gradle.properties` of the project or the root project, or even system
   * variables. If it is not found in these locations, the group defined by the [Project.setGroup] is used.
   *
   * [Reference](https://maven.apache.org/pom.html#maven-coordinates)
   */
  var group: String = project.findPropertyOrEnv("pom.group")?.toString()
    ?: project.rootPublishingDataExtensions?.group
    ?: project.group.toString()

  /**
   * The artifact id of a Maven publication.
   *
   * By default, it is searched through the `gradle.properties` of the project or the root project, or even system
   * variables. If it is not found in these locations, the [Project.getName] as artifact id.
   *
   * [Reference](https://maven.apache.org/pom.html#maven-coordinates)
   */
  var artifact: String = project.findPropertyOrEnv("pom.artifact")?.toString()
    ?: project.rootPublishingDataExtensions?.artifact
    ?: project.name

  /**
   * The version of a Maven publication.
   *
   * By default, it is searched through the `gradle.properties` of the project or the root project, or even system
   * variables. If it is not found in these locations, the version defined by the [Project.setVersion] is used.
   *
   * [Reference](https://maven.apache.org/pom.html#maven-coordinates)
   */
  var version: String = project.findPropertyOrEnv("pom.version")?.toString()
    ?: project.rootPublishingDataExtensions?.version
    ?: project.version.toString()

  /**
   * The display name of a Maven publication.
   *
   * By default, it is searched through the `gradle.properties` of the project or the root project, or even system
   * variables. If it is not found in these locations, the [artifact] as display name.
   *
   * [Reference](https://maven.apache.org/pom.html#more-project-information)
   *
   * @see MavenPom.getName
   */
  var name: String = project.findPropertyOrEnv("pom.name")?.toString()
    ?: project.rootPublishingDataExtensions?.name
    ?: artifact.capitalize()

  /**
   * The description of a Maven publication.
   *
   * By default, it is searched through the `gradle.properties` of the project or the root project, or even system
   * variables. If it is not found in these locations, the description defined by the [Project.setDescription] is used.
   *
   * [Reference](https://maven.apache.org/pom.html#more-project-information)
   *
   * @see MavenPom.getDescription
   */
  var description: String? = project.findPropertyOrEnv("pom.description")?.toString()
    ?: project.rootPublishingDataExtensions?.description
    ?: project.description

  /**
   * The project's home page.
   *
   * By default, it is searched through the `gradle.properties` of the project or the root project, or even system
   * variables. If it is not found in these locations, it will not be fill to pom.
   *
   * [Reference](https://maven.apache.org/pom.html#more-project-information)
   *
   * @see MavenPom.getUrl
   */
  var url: String? = project.findPropertyOrEnv("pom.url")?.toString()
    ?: project.rootPublishingDataExtensions?.url

  /**
   * The organization name of a Maven publication.
   *
   * By default, it is searched through the `gradle.properties` of the project or the root project, or even system
   * variables. If it is not found in these locations, it will not be fill to pom.
   *
   * [Reference](https://maven.apache.org/pom.html#organization)
   *
   * @see MavenPomOrganization.getName
   */
  var organizationName: String? = project.findPropertyOrEnv("pom.organization.name")?.toString()
    ?: project.rootPublishingDataExtensions?.organizationName

  /**
   * The organization url of a Maven publication.
   *
   * By default, it is searched through the `gradle.properties` of the project or the root project, or even system
   * variables. If it is not found in these locations, it will not be fill to pom.
   *
   * [Reference](https://maven.apache.org/pom.html#organization)
   *
   * @see MavenPomOrganization.getUrl
   */
  var organizationUrl: String? = project.findPropertyOrEnv("pom.organization.url")?.toString()
    ?: project.rootPublishingDataExtensions?.organizationUrl

  /**
   * The connection of the SCM (source control management) of a Maven publication.
   *
   * By default, it is searched through the `gradle.properties` of the project or the root project, or even system
   * variables. If it is not found in these locations, it will not be fill to pom.
   *
   * [Reference](https://maven.apache.org/pom.html#scm)
   *
   * @see MavenPomScm.getConnection
   */
  var scmConnection: String? = project.findPropertyOrEnv("pom.scm.connection")?.toString()
    ?: project.rootPublishingDataExtensions?.scmConnection

  /**
   * The url of the SCM (source control management) of a Maven publication.
   *
   * By default, it is searched through the `gradle.properties` of the project or the root project, or even system
   * variables. If it is not found in these locations, it will not be fill to pom.
   *
   * [Reference](https://maven.apache.org/pom.html#scm)
   *
   * @see MavenPomScm.getUrl
   */
  var scmUrl: String? = project.findPropertyOrEnv("pom.scm.url")?.toString()
    ?: project.rootPublishingDataExtensions?.scmUrl

  /**
   * Returns `true` if in snapshot publishing mode.
   */
  val isSnapshot: Boolean get() = version.endsWith("SNAPSHOT")

  /**
   * Configures developer data of a Maven publication with [configuration].
   *
   * [Reference](https://maven.apache.org/pom.html#developers)
   *
   * @see Developer
   */
  fun developer(configuration: Developer.() -> Unit) {
    developers += Developer().apply(configuration)
  }

  /**
   * Configures license data of a Maven publication with [configuration].
   *
   * [Reference](https://maven.apache.org/pom.html#license)
   *
   * @see License
   */
  fun license(configuration: License.() -> Unit) {
    licenses += License().apply(configuration)
  }

  /**
   * Configures other pom data of a Maven publication with [configuration].
   */
  fun configurePOM(configuration: MavenPom.() -> Unit) {
    pomConfigurations += configuration
  }

  internal val developers = mutableListOf(Developer())
  internal val licenses = mutableListOf(License())
  internal val pomConfigurations = mutableListOf<MavenPom.() -> Unit>()


  /**
   * The developer data of a Maven publication.
   *
   * [Reference](https://maven.apache.org/pom.html#developers)
   *
   * @see MavenPomDeveloper
   */
  @PublishingDataMarker
  inner class Developer {

    /**
     * The unique ID of this developer in the SCM.
     *
     * By default, it is searched through the `gradle.properties` of the project or the root project, or even system
     * variables. If it is not found in these locations, it will not be fill to pom.
     */
    var id: String? = this@PublishingData.project.findPropertyOrEnv("pom.developer.id")?.toString()
      ?: this@PublishingData.project.rootPublishingDataExtensions?.developers?.firstOrNull()?.id

    /**
     * The name of this developer.
     *
     * By default, it is searched through the `gradle.properties` of the project or the root project, or even system
     * variables. If it is not found in these locations, it will not be fill to pom.
     */
    var name: String? = this@PublishingData.project.findPropertyOrEnv("pom.developer.name")?.toString()
      ?: this@PublishingData.project.rootPublishingDataExtensions?.developers?.firstOrNull()?.name

    /**
     * The email.
     *
     * By default, it is searched through the `gradle.properties` of the project or the root project, or even system
     * variables. If it is not found in these locations, it will not be fill to pom.
     */
    var email: String? = this@PublishingData.project.findPropertyOrEnv("pom.developer.email")?.toString()
      ?: this@PublishingData.project.rootPublishingDataExtensions?.developers?.firstOrNull()?.email

    /**
     * The URL of this developer.
     *
     * By default, it is searched through the `gradle.properties` of the project or the root project, or even system
     * variables. If it is not found in these locations, it will not be fill to pom.
     */
    var url: String? = this@PublishingData.project.findPropertyOrEnv("pom.developer.url")?.toString()
      ?: this@PublishingData.project.rootPublishingDataExtensions?.developers?.firstOrNull()?.url

    /**
     * The roles of this developer.
     */
    var roles: String? = null
      ?: this@PublishingData.project.rootPublishingDataExtensions?.developers?.firstOrNull()?.roles

    /**
     * The timezone of this developer.
     */
    var timezone: String? = null
      ?: this@PublishingData.project.rootPublishingDataExtensions?.developers?.firstOrNull()?.timezone
  }

  /**
   * The license data of a Maven publication.
   *
   * [Reference](https://maven.apache.org/pom.html#licenses)
   *
   * @see MavenPomLicense
   */
  @PublishingDataMarker
  inner class License {

    /**
     * The name of this license.
     *
     * By default, it is searched through the `gradle.properties` of the project or the root project, or even system
     * variables. If it is not found in these locations, it will not be fill to pom.
     */
    var name: String? = this@PublishingData.project.findPropertyOrEnv("pom.license.name")?.toString()
      ?: this@PublishingData.project.rootPublishingDataExtensions?.licenses?.firstOrNull()?.name

    /**
     * The URL of this license.
     *
     * By default, it is searched through the `gradle.properties` of the project or the root project, or even system
     * variables. If it is not found in these locations, it will not be fill to pom.
     */
    var url: String? = this@PublishingData.project.findPropertyOrEnv("pom.license.url")?.toString()
      ?: this@PublishingData.project.rootPublishingDataExtensions?.licenses?.firstOrNull()?.url

    /**
     * The distribution of this license.
     */
    var distribution: String? = null
      ?: this@PublishingData.project.rootPublishingDataExtensions?.licenses?.firstOrNull()?.distribution

    /**
     * The comments of this license.
     */
    var comments: String? = null
      ?: this@PublishingData.project.rootPublishingDataExtensions?.licenses?.firstOrNull()?.comments
  }

  @InternalGradleToolkitApi
  companion object {
    private const val Key = "?##publishingData"

    val Project.publishingDataExtensions: PublishingData
      get() {
        extensions.addIfNotExists(Key) { PublishingData(this) }
        return extensions[Key] as PublishingData
      }

    val Project.rootPublishingDataExtensions: PublishingData?
      get() = rootProject.extensions.findByName(Key) as? PublishingData
  }
}

/**
 * Configures the publishing data of this project with [configuration].
 *
 * For more details, see:
 * [Gradle Document](https://docs.gradle.org/current/userguide/publishing_maven.html#sec:modifying_the_generated_pom)
 * [Maven Document](https://maven.apache.org/pom.html)
 */
fun Project.publishingData(configuration: PublishingData.() -> Unit = {}) =
  publishingDataExtensions.apply(configuration)