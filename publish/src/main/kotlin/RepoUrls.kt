import org.gradle.api.artifacts.repositories.PasswordCredentials

/**
 * The url of the target repository to publish to.
 *
 * @param releases The publish url of the release version of the artifact.
 * @param snapshots The publish url of the snapshot version of the artifact.
 * @param requiredCertificate Whether this repo requires a certificate [PasswordCredentials].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
open class RepoUrl(
  val releases: Any,
  val snapshots: Any = releases,
  val requiredCertificate: Boolean = true,
)

/**
 * Represents the url of the Maven Local repository.
 */
object LocalRepo : RepoUrl(releases = LocalRepo::class, requiredCertificate = false)

/**
 * Represents the url of the Sonatype OSS repository.
 *
 * @param s01 Publish to new url of Sonatype OSS, [see](https://central.sonatype.org/news/20210223_new-users-on-s01/)
 */
class SonatypeRepo(s01: Boolean = true) : RepoUrl(
  releases = host(s01) + "/service/local/staging/deploy/maven2/",
  snapshots = host(s01) + "/content/repositories/snapshots/"
) {
  private companion object {
    fun host(s01: Boolean) = when {
      s01 -> "https://s01.oss.sonatype.org"
      else -> "https://oss.sonatype.org"
    }
  }
}