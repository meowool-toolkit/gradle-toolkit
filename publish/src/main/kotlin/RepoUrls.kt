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
 * @param stagingRepositoryId Optionally specified the staging repository to publish to.
 * @param s01 Publish to new url of Sonatype OSS, [see](https://central.sonatype.org/news/20210223_new-users-on-s01/)
 */
class SonatypeRepo(
  stagingRepositoryId: String? = null,
  s01: Boolean = true,
) : RepoUrl(
  releases = host(s01) + if (stagingRepositoryId == null) "/service/local/staging/deploy/maven2/" else staging(stagingRepositoryId),
  snapshots = host(s01) + if (stagingRepositoryId == null) "/content/repositories/snapshots/" else staging(stagingRepositoryId),
) {
  private companion object {
    fun host(s01: Boolean) = when {
      s01 -> "https://s01.oss.sonatype.org"
      else -> "https://oss.sonatype.org"
    }
    fun staging(stagingRepositoryId: String) = "/service/local/staging/deployByRepositoryId/$stagingRepositoryId/"
  }
}
