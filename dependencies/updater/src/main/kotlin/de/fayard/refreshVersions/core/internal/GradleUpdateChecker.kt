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
package de.fayard.refreshVersions.core.internal

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.fayard.refreshVersions.core.extensions.okhttp.await
import de.fayard.refreshVersions.core.internal.GradleUpdateChecker.VersionType.*
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.HttpException
import retrofit2.Response

internal class GradleUpdateChecker(val httpClient: OkHttpClient) {
  private val versionsBaseUrl = "https://services.gradle.org/versions"

  @Suppress("unused") // Entries used with values()
  enum class VersionType(val apiPath: String) {
    Release("current"),
    ReleaseCandidate("release-candidate"),
    ReleaseNightly("release-nightly"),
    Nightly("nightly"),
    All("all")
  }

  private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

  suspend fun fetchGradleVersion(versionType: VersionType): List<GradleVersionModel> {
    if (versionType == All) {
      throw UnsupportedOperationException("Getting all versions of Gradle can be slow, so it's disabled.")
    }
    val url = "$versionsBaseUrl/${versionType.apiPath}"
    val request = Request.Builder().url(url).build()
    return httpClient.newCall(request).await().use { response ->
      if (response.isSuccessful) {
        when (versionType) {
          All -> {
            val listType = Types.newParameterizedType(List::class.java, GradleVersionModel::class.java)
            val jsonAdapter = moshi.adapter<List<GradleVersionModel>>(listType)
            jsonAdapter.nonNull().fromJson(response.body!!.source())!!
          }
          else -> {
            val jsonAdapter = moshi.adapter(GradleVersionModel::class.java)
            runCatching {
              listOf(jsonAdapter.nonNull().fromJson(response.body!!.source())!!)
            }.getOrDefault(emptyList())
          }
        }
      } else throw HttpException(Response.error<Any?>(response.code, response.body!!))
    }
  }
}

/**
 * Result of GET calls to endpoints in [https://services.gradle.org/versions](https://services.gradle.org/versions).
 *
 * See sample data in plugins/core/src/test/resources/gradle-current-version.json
 */
internal data class GradleVersionModel(
  val version: String,
  val buildTime: String,
  val current: Boolean,
  val snapshot: Boolean,
  val nightly: Boolean,
  val activeRc: Boolean,
  val rcFor: String,
  val milestoneFor: String,
  val broken: Boolean,
  val downloadUrl: String,
  val checksumUrl: String,
  val wrapperChecksumUrl: String? = null
)
