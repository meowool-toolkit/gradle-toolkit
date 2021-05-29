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
import extension.RootGradleDslExtension
import org.gradle.api.Project

/**
 * Use the preset configuration that conforms to the 'Meowool-Organization' specification.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun RootGradleDslExtension.useMeowoolSpec(
  isOpenSourceProject: Boolean = true,
  loadSnapshotsRepository: Boolean = false,
  enabledPublish: Boolean = true,
  publishPom: PublishPom = meowoolPublishPom(),
  publishRepo: Array<RepoUrl> = arrayOf(LocalRepo, SonatypeRepo()),
) {
  presetRepositories(loadSnapshotsRepository)
  presetKotlinCompilerArgs()
  presetPublishing(enabledPublish, publishPom, publishRepo)
  presetSpotless(isOpenSourceProject)
  presetAndroid(isOpenSourceProject)
}

/**
 * Belongs to the basic publish pom of the meowool organization specification.
 */
fun Project.meowoolPublishPom(
  group: String = findPropertyOrEnv("pom.group")?.toString() ?: this.group.toString(),
  artifact: String = findPropertyOrEnv("pom.artifact")?.toString() ?: this.name,
  version: String = findPropertyOrEnv("pom.version")?.toString() ?: this.version.toString(),
  name: String = findPropertyOrEnv("pom.name")?.toString() ?: artifact,
  description: String? = findPropertyOrEnv("pom.description")?.toString(),
  url: String? = findPropertyOrEnv("pom.url")?.toString(),
  developerId: String? = findPropertyOrEnv("pom.developer.id")?.toString(),
  developerName: String? = findPropertyOrEnv("pom.developer.name")?.toString(),
  developerUrl: String? = findPropertyOrEnv("pom.developer.url")?.toString(),
  scmConnection: String? = findPropertyOrEnv("pom.scm.connection")?.toString(),
  scmUrl: String? = findPropertyOrEnv("pom.scm.url")?.toString(),
) = publishPom(group,
  artifact,
  version,
  name,
  description,
  url,
  licenseName = "The Apache Software License, Version 2.0",
  licenseUrl = "https://github.com/meowool/license/blob/main/LICENSE",
  developerId ?: "meowool",
  developerName ?: "Meowool Organization",
  developerUrl ?: "https://github.com/meowool/",
  organizationName = "Meowool",
  organizationUrl = "https://github.com/meowool/",
  scmConnection,
  scmUrl
)

/**
 * Belongs to the basic publish pom of the meowool organization specification.
 */
fun RootGradleDslExtension.meowoolPublishPom(
  group: String = project.findPropertyOrEnv("pom.group")?.toString() ?: project.group.toString(),
  artifact: String = project.findPropertyOrEnv("pom.artifact")?.toString() ?: project.name,
  version: String = project.findPropertyOrEnv("pom.version")?.toString() ?: project.version.toString(),
  name: String = project.findPropertyOrEnv("pom.name")?.toString() ?: artifact,
  description: String? = project.findPropertyOrEnv("pom.description")?.toString(),
  url: String? = project.findPropertyOrEnv("pom.url")?.toString(),
  developerId: String? = project.findPropertyOrEnv("pom.developer.id")?.toString(),
  developerName: String? = project.findPropertyOrEnv("pom.developer.name")?.toString(),
  developerUrl: String? = project.findPropertyOrEnv("pom.developer.url")?.toString(),
  scmConnection: String? = project.findPropertyOrEnv("pom.scm.connection")?.toString(),
  scmUrl: String? = project.findPropertyOrEnv("pom.scm.url")?.toString(),
) = project.meowoolPublishPom(group,
  artifact,
  version,
  name,
  description,
  url,
  developerId,
  developerName,
  developerUrl,
  scmConnection,
  scmUrl)