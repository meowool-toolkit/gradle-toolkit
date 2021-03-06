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

import com.google.auth.Credentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.NoCredentials
import com.google.cloud.http.BaseHttpServiceException
import com.google.cloud.storage.Blob
import com.google.cloud.storage.Bucket
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import de.fayard.refreshVersions.core.ModuleId
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

internal class MavenDependencyVersionsFetcherGoogleCloudStorage(
  moduleId: ModuleId,
  repoUrl: String
) : MavenDependencyVersionsFetcher(
  moduleId = moduleId,
  repoUrl = repoUrl
) {
  override suspend fun getXmlMetadataOrNull(): String? {
    val path: String = with(moduleId) {
      "$repoPath/${group!!.replace('.', '/')}/$name/maven-metadata.xml"
    }
    return try {
      bucket.get(path)?.let { blob: Blob -> String(blob.getContent()) }
    } catch (e: BaseHttpServiceException) {
      if (e.code == 404) {
        null // Also see https://github.com/googleapis/google-cloud-java/issues/3402
      } else throw IOException(
        "Unable to load '$path' from Google Cloud Storage bucket '$bucketName'.",
        e
      )
    }
  }

  private val bucketName = repoUrl.substringAfter("gcs://").substringBefore("/")
  private val repoPath = repoUrl.substringAfter("gcs://").substringAfter("/")

  private val bucket: Bucket by lazy {
    val credentials: Credentials = try {
      CREDENTIALS_PATH?.let {
        ServiceAccountCredentials.fromStream(FileInputStream(it))
      } ?: NoCredentials.getInstance()
    } catch (e: FileNotFoundException) {
      NoCredentials.getInstance()
    }
    try {
      val storage: Storage = StorageOptions.newBuilder()
        .setCredentials(credentials)
        .build()
        .service

      storage.get(bucketName)
        ?: throw NoSuchElementException("The Google Cloud Storage bucket $bucketName wasn't found.")
    } catch (e: IOException) {
      throw IOException("Unable to access Google Cloud Storage bucket '$bucketName'.", e)
    } catch (e: BaseHttpServiceException) {
      throw IOException("Unable to access Google Cloud Storage bucket '$bucketName'.", e)
    }
  }

  companion object {
    private val CREDENTIALS_PATH: String? = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
  }
}
