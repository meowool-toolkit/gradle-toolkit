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
import org.gradle.api.publish.maven.MavenPom

/**
 * A lazy pom store, use [configuration] when publishing.
 *
 * [For more details](https://docs.gradle.org/current/userguide/publishing_maven.html#sec:modifying_the_generated_pom)
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class PublishPom internal constructor(
  val group: String,
  val artifact: String,
  val version: String,
  val description: String?,
  val configuration: MavenPom.() -> Unit,
) {

  /**
   * Returns `true` if in snapshot mode.
   */
  val isSnapshot: Boolean get() = version.endsWith("SNAPSHOT")
}

/**
 * Creates a pom to maven publish.
 * [For more details](https://docs.gradle.org/current/userguide/publishing_maven.html#sec:modifying_the_generated_pom)
 */
fun publishPom(
  group: String,
  artifact: String,
  version: String,
  description: String? = null,
  configuration: MavenPom.() -> Unit,
): PublishPom = PublishPom(group, artifact, version, description, configuration)

/**
 * Creates a basic pom to maven publish.
 * [For more details](https://docs.gradle.org/current/userguide/publishing_maven.html#sec:modifying_the_generated_pom)
 *
 * @param configuration the extra configuration of pom.
 *
 * @see MavenPom for more details.
 */
fun Project.publishPom(
  group: String = findPropertyOrEnv("pom.group")?.toString() ?: this.group.toString(),
  artifact: String = findPropertyOrEnv("pom.artifact")?.toString() ?: this.name,
  version: String = findPropertyOrEnv("pom.version")?.toString() ?: this.version.toString(),
  name: String = findPropertyOrEnv("pom.name")?.toString() ?: artifact,
  description: String? = findPropertyOrEnv("pom.description")?.toString() ?: this.description,
  url: String? = findPropertyOrEnv("pom.url")?.toString(),
  licenseName: String? = findPropertyOrEnv("pom.license.name")?.toString(),
  licenseUrl: String? = findPropertyOrEnv("pom.license.url")?.toString(),
  developerId: String? = findPropertyOrEnv("pom.developer.id")?.toString(),
  developerName: String? = findPropertyOrEnv("pom.developer.name")?.toString(),
  developerUrl: String? = findPropertyOrEnv("pom.developer.url")?.toString(),
  organizationName: String? = findPropertyOrEnv("pom.organization.name")?.toString(),
  organizationUrl: String? = findPropertyOrEnv("pom.organization.url")?.toString(),
  scmConnection: String? = findPropertyOrEnv("pom.scm.connection")?.toString(),
  scmUrl: String? = findPropertyOrEnv("pom.scm.url")?.toString(),
  configuration: MavenPom.() -> Unit = {},
): PublishPom = PublishPom(group, artifact, version, description) {
  getName().set(name)
  getUrl().set(url)
  description?.let(getDescription()::set)
  licenses {
    license {
      licenseName?.let(getName()::set)
      licenseUrl?.let(getUrl()::set)
    }
  }
  developers {
    developer {
      developerId?.let(id::set)
      developerName?.let(getName()::set)
      developerUrl?.let(getUrl()::set)
    }
  }
  organization {
    organizationName?.let(getName()::set)
    organizationUrl?.let(getUrl()::set)
  }
  scm {
    scmConnection?.let(connection::set)
    scmUrl?.let(getUrl()::set)
  }
  configuration()
}
