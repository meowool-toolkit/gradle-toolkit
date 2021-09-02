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
import org.gradle.api.artifacts.repositories.PasswordCredentials
import java.io.File
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolutePathString

/**
 * The url of the target repository to publish to.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
abstract class RepoUrl {

  /**
   * The publishing url of the artifact.
   */
  abstract val url: Any

  /**
   * Whether this repo requires a certificate [PasswordCredentials].
   */
  abstract val requiredCertificate: Boolean

  /**
   * Represents the artifacts to publish is snapshot version.
   */
  var isSnapshot: Boolean = false
    internal set

  /**
   * The project to uses this repo url.
   */
  lateinit var project: Project
}

/**
 * The default implementation to [RepoUrl].
 */
open class DefaultRepoUrl(
  private val releases: Any,
  override val requiredCertificate: Boolean,
  private val snapshots: Any = releases,
) : RepoUrl() {
  override val url: Any
    get() = if (isSnapshot) snapshots else releases
}

/**
 * Represents the url of the Maven Local repository.
 */
object LocalRepo : DefaultRepoUrl(releases = LocalRepo::class, requiredCertificate = false)

/**
 * Represents the url of the repository of specified path.
 *
 * @param releasesPath The path to releases repository
 * @param snapshotsPath The path to snapshots repository
 */
class PathRepo constructor(
  private val releasesPath: String,
  private val snapshotsPath: String = releasesPath,
) : DefaultRepoUrl(releasesPath, requiredCertificate = false, snapshotsPath) {

  /**
   * Represents the url of the repository of specified file (directory).
   *
   * @param releases The file (directory) to releases repository
   * @param snapshots The file (directory) to snapshots repository
   */
  @OptIn(ExperimentalPathApi::class)
  constructor(releases: Path, snapshots: Path = releases): this(
    releases.normalize().absolutePathString(),
    snapshots.normalize().absolutePathString()
  )

  /**
   * Represents the url of the repository of specified file (directory).
   *
   * @param releases The file (directory) to releases repository
   * @param snapshots The file (directory) to snapshots repository
   */
  constructor(releases: File, snapshots: File = releases): this(
    releases.normalize().absolutePath,
    snapshots.normalize().absolutePath
  )
}

/**
 * Represents the url of the Sonatype OSS repository.
 *
 * @param s01 Publish to new url of Sonatype OSS, [see](https://central.sonatype.org/news/20210223_new-users-on-s01/)
 */
class SonatypeRepo(private val s01: Boolean = true) : RepoUrl() {
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
