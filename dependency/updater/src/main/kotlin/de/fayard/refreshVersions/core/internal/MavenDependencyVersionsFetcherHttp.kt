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

import de.fayard.refreshVersions.core.ModuleId
import de.fayard.refreshVersions.core.extensions.okhttp.await
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.HttpException
import retrofit2.Response

internal class MavenDependencyVersionsFetcherHttp(
  private val httpClient: OkHttpClient,
  moduleId: ModuleId,
  val repoUrl: String,
  repoAuthorization: String?
) : MavenDependencyVersionsFetcher(
  moduleId = moduleId,
  repoUrl = repoUrl
) {

  init {
    require(repoUrl.endsWith('/'))
  }

  private val request = Request.Builder().apply {
    val metadataUrlForArtifact = moduleId.let { (group, name) ->
      "$repoUrl${group!!.replace('.', '/')}/$name/maven-metadata.xml"
    }
    url(metadataUrlForArtifact)
    header(
      name = "Authorization",
      value = repoAuthorization ?: return@apply
    )
  }.build()

  override suspend fun getXmlMetadataOrNull(): String? {
    return httpClient.newCall(request).await().use { response ->
      if (response.isSuccessful) {
        response.use { it.body!!.string() }
      } else when (response.code) {
        403 -> when {
          repoUrl.let {
            it.startsWith("https://dl.bintray.com") || it.startsWith("https://jcenter.bintray.com")
          } -> null // Artifact not available on jcenter nor bintray, post "sunset" announcement.
          else -> throw HttpException(Response.error<Any?>(response.code, response.body!!))
        }
        404 -> null // Normal not found result
        401 -> null // Returned by some repositories that have optional authentication (like jitpack.io)
        else -> throw HttpException(Response.error<Any?>(response.code, response.body!!))
      }
    }
  }
}
