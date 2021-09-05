import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import java.io.File
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolutePathString

/**
 * The repository destination to publish to.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
abstract class PublishingDestination {

  /**
   * The publishing destination url of the publication.
   */
  abstract val url: Any

  /**
   * Whether this publishing destination requires a certificate [PasswordCredentials].
   *
   * [Reference](https://docs.gradle.org/current/samples/sample_publishing_credentials.html)
   */
  abstract val requiredCertificate: Boolean

  /**
   * The project to uses this publishing destination.
   */
  lateinit var project: Project

  /**
   * Returns `true` if the publication to be published is a snapshot version.
   */
  var isSnapshot: Boolean = false
    internal set
}

/**
 * The default implementation to [PublishingDestination].
 */
open class BasePublishingDestination(
  private val releases: Any,
  override val requiredCertificate: Boolean,
  private val snapshots: Any = releases,
) : PublishingDestination() {
  override val url: Any
    get() = if (isSnapshot) snapshots else releases

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is BasePublishingDestination) return false

    if (releases != other.releases) return false
    if (requiredCertificate != other.requiredCertificate) return false
    if (snapshots != other.snapshots) return false

    return true
  }

  override fun hashCode(): Int {
    var result = releases.hashCode()
    result = 31 * result + requiredCertificate.hashCode()
    result = 31 * result + snapshots.hashCode()
    return result
  }
}

/**
 * The publishing destination for the Maven Local repository.
 */
object MavenLocalDestination : BasePublishingDestination(
  releases = MavenLocalDestination::class,
  requiredCertificate = false
) {
  override fun equals(other: Any?): Boolean = this === other
}

/**
 * The publishing destination for the repository of specified directory.
 *
 * @param releasesPath The directory path to releases repository.
 * @param snapshotsPath The directory path to snapshots repository.
 */
data class DirectoryDestination constructor(
  private val releasesPath: String,
  private val snapshotsPath: String = releasesPath,
) : BasePublishingDestination(releasesPath, requiredCertificate = false, snapshotsPath) {

  /**
   * The publishing destination for the repository of specified path of directory.
   *
   * @param releases The directory path to releases repository.
   * @param snapshots The directory path to snapshots repository.
   */
  @OptIn(ExperimentalPathApi::class)
  constructor(releases: Path, snapshots: Path = releases): this(
    releases.normalize().absolutePathString(),
    snapshots.normalize().absolutePathString()
  )

  /**
   * The publishing destination for the repository of specified file of directory.
   *
   * @param releases The file (directory) to releases repository.
   * @param snapshots The file (directory) to snapshots repository.
   */
  constructor(releases: File, snapshots: File = releases): this(
    releases.normalize().absolutePath,
    snapshots.normalize().absolutePath
  )
}

/**
 * The publishing destination for the Sonatype OSS repository.
 *
 * @param s01 Publish to new url of Sonatype OSS, [see](https://central.sonatype.org/news/20210223_new-users-on-s01/)
 */
class SonatypeDestination(private val s01: Boolean = true) : PublishingDestination() {
  val baseUrl = when {
    s01 -> "https://s01.oss.sonatype.org"
    else -> "https://oss.sonatype.org"
  }

  override val url: Any
    get() = when {
      isSnapshot -> "$baseUrl/content/repositories/snapshots/"
      stagingId.isNullOrEmpty() -> "$baseUrl/service/local/staging/deploy/maven2/"
      else -> "$baseUrl/service/local/staging/deployByRepositoryId/$stagingId/"
    }

  override val requiredCertificate: Boolean = true

  internal var stagingId: String? = null

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is SonatypeDestination) return false

    if (s01 != other.s01) return false
    if (baseUrl != other.baseUrl) return false
    if (requiredCertificate != other.requiredCertificate) return false
    if (stagingId != other.stagingId) return false

    return true
  }

  override fun hashCode(): Int {
    var result = s01.hashCode()
    result = 31 * result + baseUrl.hashCode()
    result = 31 * result + requiredCertificate.hashCode()
    result = 31 * result + (stagingId?.hashCode() ?: 0)
    return result
  }
}
