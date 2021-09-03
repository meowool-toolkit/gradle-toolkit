import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import java.io.File
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolutePathString

/**
 * The target repository to publish to.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
abstract class PublishingTarget {

  /**
   * The publishing url of the artifact.
   */
  abstract val url: Any

  /**
   * Whether this repo requires a certificate [PasswordCredentials].
   *
   * [Reference](https://docs.gradle.org/current/samples/sample_publishing_credentials.html)
   */
  abstract val requiredCertificate: Boolean

  /**
   * The project to uses this repo url.
   */
  lateinit var project: Project

  /**
   * Represents the artifacts to publish is snapshot version.
   */
  var isSnapshot: Boolean = false
    internal set
}

/**
 * The default implementation to [PublishingTarget].
 */
open class BasePublishingTarget(
  private val releases: Any,
  override val requiredCertificate: Boolean,
  private val snapshots: Any = releases,
) : PublishingTarget() {
  override val url: Any
    get() = if (isSnapshot) snapshots else releases
}

/**
 * The publishing target for the Maven Local repository.
 */
object MavenLocalTarget : BasePublishingTarget(releases = MavenLocalTarget::class, requiredCertificate = false)

/**
 * The publishing target for the repository of specified directory.
 *
 * @param releasesPath The directory path to releases repository.
 * @param snapshotsPath The directory path to snapshots repository.
 */
class DirectoryTarget constructor(
  private val releasesPath: String,
  private val snapshotsPath: String = releasesPath,
) : BasePublishingTarget(releasesPath, requiredCertificate = false, snapshotsPath) {

  /**
   * The publishing target for the repository of specified path of directory.
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
   * The publishing target for the repository of specified file of directory.
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
 * The publishing target for the Sonatype OSS repository.
 *
 * @param s01 Publish to new url of Sonatype OSS, [see](https://central.sonatype.org/news/20210223_new-users-on-s01/)
 */
class SonatypeTarget(private val s01: Boolean = true) : PublishingTarget() {
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
}
