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

 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
package de.fayard.refreshVersions.core.internal

import de.fayard.refreshVersions.core.DependencyVersionsFetcher
import de.fayard.refreshVersions.core.ModuleId
import de.fayard.refreshVersions.core.extensions.gradle.passwordCredentials
import okhttp3.Credentials
import okhttp3.OkHttpClient
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.repositories.MavenArtifactRepository

internal operator fun DependencyVersionsFetcher.Companion.invoke(
  httpClient: OkHttpClient,
  dependency: Dependency,
  repository: MavenArtifactRepository // TODO: Support Ivy repositories
): DependencyVersionsFetcher? {
  val group = dependency.group ?: return null // TODO: Support NPM dependencies from Kotlin/JS
  val name = dependency.name
  return when (repository.url.scheme) {
    "https" -> MavenDependencyVersionsFetcherHttp(
      httpClient = httpClient,
      moduleId = ModuleId(group, name),
      repoUrl = repository.url.toString().let { if (it.endsWith('/')) it else "$it/" },
      repoAuthorization = repository.passwordCredentials?.let { credentials ->
        Credentials.basic(
          username = credentials.username ?: return@let null,
          password = credentials.password ?: return@let null
        )
      }
    )
    "file" -> MavenDependencyVersionsFetcherFile(
      moduleId = ModuleId(group, name),
      repoUrl = repository.url.toString().let { if (it.endsWith('/')) it else "$it/" }
    )
    "gcs" -> MavenDependencyVersionsFetcherGoogleCloudStorage(
      moduleId = ModuleId(group, name),
      repoUrl = repository.url.toString()
    )
    "http" -> null // TODO: Show non fatal error that http is not supported for security reasons.
    else -> null // TODO: Support more transport protocols. Here's what Gradle supports:
    // https://docs.gradle.org/current/userguide/declaring_repositories.html#sec:supported_transport_protocols
    // We should trigger a warning that it's not supported yet, with link to relevant issue,
    // and we should report any transport protocol not known to be supported by Gradle (but not crash
    // in case a future Gradle version adds more of them and we're not updated yet to support it).
  }
}
