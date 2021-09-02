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
@file:Suppress("FunctionName")

import PublishingData.Companion.publishingDataExtensions
import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaTask

/**
 * Use the preset configuration that conforms to the 'Meowool-Organization' specification.
 *
 * @param isOpenSourceProject Set the value to `true` if you are developing an open source project.
 * @param loadSnapshotsRepository Load the snapshots-repository for each project.
 * @param enabledPublish Enabled the publish feature for each project. (root project whether the enabled depending
 *   on [publishRootProject])
 * @param publishRootProject Whether rootProject also enabled the publishing feature, if `false`, rootProject will not
 *   apply the `maven-publish` plugin.
 * @param publishRootProject Whether the project with the android plugin applied need to be publishing.
 * @param publishReleaseSigning Whether to signing the artifact of release version before publishing.
 * @param publishSnapshotSigning Whether to signing the artifact of snapshot version before publishing.
 * @param publishRepo The repository(s) to publish to.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun GradleToolkitExtension.useMeowoolSpec(
  isOpenSourceProject: Boolean = true,
  loadSnapshotsRepository: Boolean = false,
  enabledPublish: Boolean = true,
  publishRootProject: Boolean = true,
  publishAndroidAppProject: Boolean = false,
  publishReleaseSigning: Boolean = true,
  publishSnapshotSigning: Boolean = false,
  publishRepo: Array<RepoUrl> = arrayOf(SonatypeRepo()),
) {
  dependencyMapperPrebuilt()
  presetRepositories(loadSnapshotsRepository)
  presetKotlinCompilerArgs()
  presetPublishing(
    enabledPublish,
    publishRootProject,
    publishAndroidAppProject,
    publishReleaseSigning,
    publishSnapshotSigning,
    publishRepo,
  )
  presetSpotless(if (isOpenSourceProject) OpenSourceLicense else null)
  presetAndroid(isOpenSourceProject)
}

/**
 * Use the spotless configuration preset by 'Meowool-Organization'.
 *
 * @param licenseHeader The license header of the source code, if it is `null`, it will not be imported during
 *   spotless running.
 * @param configuration The extra configuration.
 */
fun GradleToolkitExtension.useMeowoolSpotlessSpec(
  licenseHeader: String? = OpenSourceLicense,
  configuration: SpotlessExtension.() -> Unit = {},
) = presetSpotless(licenseHeader, configuration)

/**
 * Use 'Meowool-Organization' preset publish configuration.
 *
 * @see Project.mavenPublish For more details
 */
fun Project.meowoolMavenPublish(
  repo: Array<RepoUrl> = arrayOf(SonatypeRepo()),
  releaseSigning: Boolean = true,
  snapshotSigning: Boolean = false,
  enabledPublish: Boolean = true,
) = afterEvaluate {
  if (enabledPublish) {
    meowoolPublishingData()
    mavenPublish(repo, releaseSigning, snapshotSigning)
    tasks.withType<DokkaTask> {
      dokkaSourceSets.configureEach {
        skipDeprecated.set(true)
        skipEmptyPackages.set(false)
      }
    }
  }

  // Keep spotless before publish.
  val spotlessApply = tasks.findByName("spotlessApply") ?: return@afterEvaluate
  tasks.findByName("publish")?.dependsOn(spotlessApply)
}

/**
 * Configures the publishing data belongs to the 'Meowool-Organization' specification to this project.
 */
fun Project.meowoolPublishingData(configuration: PublishingData.() -> Unit = {}) = publishingDataExtensions.apply {
  license {
    name = "The Apache Software License, Version 2.0"
    url = "https://github.com/meowool/license/blob/main/LICENSE"
  }
  developer {
    id = "meowool"
    name = "Meowool Organization"
    url = "https://github.com/meowool/"
  }
  organizationName = "Meowool"
  organizationUrl = "https://github.com/meowool/"
  configuration()
}