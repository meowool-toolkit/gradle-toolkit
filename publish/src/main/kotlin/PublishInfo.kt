import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPom

/**
 * A lazy pom store, use [configuration] when publishing.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class PublishPom internal constructor(
  val group: String,
  val artifact: String,
  val version: String,
  val configuration: MavenPom.() -> Unit,
) {

  /**
   * Returns `true` if in snapshot mode.
   */
  val isSnapshot: Boolean get() = version.endsWith("SNAPSHOT")
}

/**
 * Creates a pom to maven publish.
 */
fun publishPom(
  group: String,
  artifact: String,
  version: String,
  configuration: MavenPom.() -> Unit,
): PublishPom = PublishPom(group, artifact, version, configuration)

/**
 * Creates a basic pom to maven publish.
 *
 * @param configuration the extra configuration of pom.
 *
 * @see MavenPom for more details.
 */
fun Project.publishPom(
  group: String = findPropertyOrEnv("pom.group")?.toString() ?: this.group.toString(),
  artifact: String = findPropertyOrEnv("pom.artifact")?.toString() ?: this.name,
  version: String = findPropertyOrEnv("pom.version")?.toString() ?: this.version.toString(),
  name: String = findPropertyOrEnv("pom.name")?.toString() ?: artifact,
  description: String? = findPropertyOrEnv("pom.description")?.toString(),
  url: String? = findPropertyOrEnv("pom.url")?.toString(),
  licenseName: String? = findPropertyOrEnv("pom.license.name")?.toString(),
  licenseUrl: String? = findPropertyOrEnv("pom.license.url")?.toString(),
  developerId: String? = findPropertyOrEnv("pom.developer.id")?.toString(),
  developerName: String? = findPropertyOrEnv("pom.developer.name")?.toString(),
  developerUrl: String? = findPropertyOrEnv("pom.developer.url")?.toString(),
  organizationName: String? = findPropertyOrEnv("pom.organization.name")?.toString(),
  organizationUrl: String? = findPropertyOrEnv("pom.organization.url")?.toString(),
  scmConnection: String? = findPropertyOrEnv("pom.scm.connection")?.toString(),
  scmUrl: String? = findPropertyOrEnv("pom.scm.url")?.toString(),
  configuration: MavenPom.() -> Unit = {},
): PublishPom = PublishPom(group, artifact, version) {
  getName().set(name)
  getDescription().set(description)
  getUrl().set(url)
  licenses {
    licenses {
      licenseName?.let(getName()::set)
      licenseUrl?.let(getUrl()::set)
    }
  }
  developers {
    developer {
      developerId?.let(id::set)
      developerName?.let(getName()::set)
      developerUrl?.let(getUrl()::set)
    }
  }
  organization {
    organizationName?.let(getName()::set)
    organizationUrl?.let(getUrl()::set)
  }
  scm {
    scmConnection?.let(connection::set)
    scmUrl?.let(getUrl()::set)
  }
  configuration()
}