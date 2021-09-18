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
@file:Suppress("MemberVisibilityCanBePrivate", "DEPRECATION", "unused")

package com.meowool.gradle.toolkit.publisher

import com.meowool.gradle.toolkit.publisher.internal.maxOf
import com.meowool.gradle.toolkit.publisher.internal.parentPublicationData
import com.meowool.sweekt.ifNull
import findPropertyOrEnv
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPomDeveloper
import org.gradle.api.publish.maven.MavenPomLicense

/**
 * A data for a Maven publication or Gradle plugin.
 *
 * Note that the data related to the Gradle plugin will only take effect when [PublicationExtension.pluginClass] is
 * not `null`, otherwise they will only be regarded as data of Maven publication.
 *
 * For more details, see:
 * [Maven Document](https://maven.apache.org/pom.html)
 * [Gradle Maven](https://docs.gradle.org/current/userguide/publishing_maven.html)
 * [Gradle Plugin](https://docs.gradle.org/current/userguide/publishing_gradle_plugins.html)
 * [MavenPom API](https://docs.gradle.org/current/dsl/org.gradle.api.publish.maven.MavenPom.html)
 *
 * @author 凛 (https://github.com/RinOrz)
 * @see MavenPom for more details.
 */
@PublicationData.Marker class PublicationData(private val project: Project) {

  /**
   * The group id of a Maven publication or the prefix of Gradle plugin id.
   *
   * If this value is `null`, it is searched with `publication.groupId` through the `gradle.properties` of the
   * project or the root project, or even system environment variables. If it is not found in these locations, the
   * group defined by the [Project.setGroup] is used.
   *
   * For the Maven publication, it is equivalent to:
   * ```
   * publishing {
   *   publications {
   *     create<MavenPublication>(...) {
   *       groupId = ???
   *     }
   *   }
   * }
   * ```
   *
   * For the Gradle plugin, it is equivalent to:
   * ```
   * gradlePlugin {
   *   plugins {
   *     create(...) {
   *       id = ??? + "." + artifactId
   *     }
   *   }
   * }
   * ```
   *
   * @see groupIdOrDefault
   */
  var groupId: String? = null
    set(value) {
      field = value
      project.group = groupIdOrDefault()
      project.allprojects { group = groupIdOrDefault() }
    }

  /**
   * The artifact id of a Maven publication or the suffix of Gradle plugin id.
   *
   * If this value is `null`, it is searched with `publication.artifactId` through the `gradle.properties` of the
   * project or the root project, or even system environment variables. If it is not found in these locations, the
   * [Project.getName] as artifact id.
   *
   * For the Maven publication, it is equivalent to:
   * ```
   * publishing {
   *   publications {
   *     create<MavenPublication>(...) {
   *       artifactId = ???
   *     }
   *   }
   * }
   * ```
   *
   * For the Gradle plugin, it is equivalent to:
   * ```
   * gradlePlugin {
   *   plugins {
   *     create(...) {
   *       id = groupId + "." + ???
   *     }
   *   }
   * }
   * ```
   *
   * @see artifactIdOrDefault
   */
  var artifactId: String? = null

  /**
   * The plugin id of a Gradle plugin.
   *
   * If this value is `null`, it is searched with `publication.pluginId` through the `gradle.properties` of the
   * project or the root project, or even system environment variables. If it is not found in these locations, use
   * [groupId] + [artifactId] as the plugin id.
   *
   * It is equivalent to:
   * ```
   * gradlePlugin {
   *   plugins {
   *     create(...) {
   *       id = groupId + "." + artifactId
   *     }
   *   }
   * }
   * ```
   *
   * @see pluginIdOrDefault
   */
  var pluginId: String? = null

  /**
   * The version of a Maven publication or Gradle plugin.
   *
   * If this value is `null`, it is searched with `publication.version` through the `gradle.properties` of the project
   * or the root project, or even system environment variables. If it is not found in these locations, the version
   * defined by the [Project.setVersion] is used.
   *
   * For the Maven publication, it is equivalent to:
   * ```
   * publishing {
   *   publications {
   *     create<MavenPublication>(...) {
   *       version = ???
   *     }
   *   }
   * }
   * ```
   *
   * For the Gradle plugin, it is equivalent to [Project.setVersion].
   *
   * @see versionOrDefault
   */
  var version: String? = null
    set(value) {
      field = value
      project.version = versionOrDefault()
      project.allprojects { version = versionOrDefault() }
    }

  /**
   * The display name of a Maven publication or Gradle plugin.
   *
   * If this value is `null`, it is searched with `publication.displayName` through the `gradle.properties` of the
   * project or the root project, or even system environment variables. If it is not found in these locations, the
   * capitalized [artifactId] as display name.
   *
   * For the Maven publication, it is equivalent to:
   * ```
   * publishing {
   *   publications {
   *     create<MavenPublication>(...) {
   *       pom {
   *         name.set(???)
   *       }
   *     }
   *   }
   * }
   * ```
   *
   * For the Gradle plugin, it is equivalent to:
   * ```
   * gradlePlugin {
   *   plugins {
   *     create(...) {
   *       displayName = ???
   *     }
   *   }
   * }
   * ```
   *
   * @see displayNameOrDefault
   */
  var displayName: String? = null

  /**
   * The description of a Maven publication or Gradle plugin.
   *
   * If this value is `null`, it is searched with `publication.description` through the `gradle.properties` of the
   * project or the root project, or even system environment variables. If it is not found in these locations, the
   * description defined by the [Project.setDescription] is used.
   *
   *
   * For the Maven publication, it is equivalent to:
   * ```
   * publishing {
   *   publications {
   *     create<MavenPublication>(...) {
   *       pom {
   *         description.set(???)
   *       }
   *     }
   *   }
   * }
   * ```
   *
   * For the Gradle plugin, it is equivalent to:
   * ```
   * gradlePlugin {
   *   plugins {
   *     create(...) {
   *       description = ???
   *     }
   *   }
   * }
   * ```
   *
   * @see descriptionOrDefault
   */
  var description: String? = null
    set(value) {
      field = value
      project.description = descriptionOrDefault()
      project.allprojects { description = descriptionOrDefault() }
    }

  /**
   * The website URL of a Maven publication or Gradle plugin.
   *
   * If this value is `null`, it is searched with `publication.url` through the `gradle.properties` of the project or
   * the root project, or even system environment variables. If it is not found in these locations, the [vcs] as
   * the website URL.
   *
   * For the Maven publication, it is equivalent to:
   * ```
   * publishing {
   *   publications {
   *     create<MavenPublication>(...) {
   *       pom {
   *         url.set(???)
   *       }
   *     }
   *   }
   * }
   * ```
   *
   * For the Gradle plugin, it is equivalent to:
   * ```
   * pluginBundle {
   *   website = ???
   * }
   * ```
   *
   * @see urlOrDefault
   */
  var url: String? = null

  /**
   * The VCS (aka SCM) URL of a Maven publication or Gradle plugin.
   *
   * If this value is `null`, it is searched with `publication.vcs` through the `gradle.properties` of the project
   * or the root project, or even system environment variables. If it is not found in these locations, it will not be
   * bind to the publishing.
   *
   * For the Maven publication, it is equivalent to:
   * ```
   * publishing {
   *   publications {
   *     create<MavenPublication>(...) {
   *       pom {
   *         scm {
   *           url.set(???)
   *         }
   *       }
   *     }
   *   }
   * }
   * ```
   *
   * For the Gradle plugin, it is equivalent to:
   * ```
   * pluginBundle {
   *   vcsUrl = ???
   * }
   * ```
   *
   * @see vcsOrDefault
   */
  var vcs: String? = null

  /**
   * The tags of a Gradle plugin.
   *
   * By default, it will search for the value of `publication.tags` from the `gradle.properties` of the project and root
   * project, or even the system environment variables and add tags. Multiple tag values are separated by `, ` example:
   * `publication.tags=a, b, c`
   *
   * It is equivalent to:
   * ```
   * pluginBundle {
   *   tags = ???
   * }
   * ```
   *
   * @see tagsOrDefault
   */
  var tags: MutableSet<String> = mutableSetOf()

  /**
   * The organization name of a Maven publication.
   *
   * If this value is `null`, it is searched with `publication.organizationName` through the `gradle.properties` of
   * the project or the root project, or even system environment variables. If it is not found in these locations, it
   * will not be bind to the publishing.
   *
   * @see organizationNameOrDefault
   */
  var organizationName: String? = null

  /**
   * The organization URL of a Maven publication.
   *
   * If this value is `null`, it is searched with `publication.organizationUrl` through the `gradle.properties` of
   * the project or the root project, or even system environment variables. If it is not found in these locations, it
   * will not be bind to the publishing.
   *
   * @see organizationUrlOrDefault
   */
  var organizationUrl: String? = null

  /**
   * The data of developers of a Maven publication.
   *
   * By default, the following keys are used to search through the `gradle.properties` of the project or the root
   * project, or even system environment variables, as the developers data. These values of multiple developers are
   * separated by `, `:
   * ```
   * `publication.developers.id` example: `0, 2`
   * `publication.developers.name` example: `Dev A, Dev B`
   * `publication.developers.email` example: `123@email.com, 321@email.com`
   * `publication.developers.url` example: `https://devA.website, https://devB.website`
   * ```
   *
   * It is equivalent to:
   * ```
   * publishing {
   *   publications {
   *     create<MavenPublication>(...) {
   *       pom {
   *         developers {
   *           developer { ??? }
   *           ...
   *         }
   *       }
   *     }
   *   }
   * }
   * ```
   *
   * @see developersOrDefault
   */
  val developers: MutableList<Developer> = mutableListOf()

  /**
   * The data of licenses of a Maven publication.
   *
   * By default, the following keys are used to search through the `gradle.properties` of the project or the root
   * project, or even system environment variables, as the licenses data. These values of multiple licenses are
   * separated by `, `:
   * ```
   * `publication.licenses.name` example: `Apache License 2, MIT License`
   * `publication.licenses.url` example: `https://www.apache.org/licenses/LICENSE-2.0.txt, https://opensource.org/licenses/MIT`
   * ```
   *
   * It is equivalent to:
   * ```
   * publishing {
   *   publications {
   *     create<MavenPublication>(...) {
   *       pom {
   *         licenses {
   *           license { ??? }
   *           ...
   *         }
   *       }
   *     }
   *   }
   * }
   * ```
   *
   * @see licensesOrDefault
   */
  val licenses: MutableList<License> = mutableListOf()

  internal val pomConfigurations: MutableList<MavenPom.() -> Unit> = mutableListOf()

  /**
   * Adds [tags] to Gradle plugin.
   */
  fun tags(vararg tags: String) {
    this.tags += tags
  }

  /**
   * Adds a developer data of a Maven publication with [configuration].
   *
   * [Reference](https://maven.apache.org/pom.html#developers)
   *
   * @see Developer
   */
  fun developer(configuration: Developer.() -> Unit) {
    developers += Developer().apply(configuration)
  }

  /**
   * Adds a license data of a Maven publication with [configuration].
   *
   * [Reference](https://maven.apache.org/pom.html#license)
   *
   * @see License
   */
  fun license(configuration: License.() -> Unit) {
    licenses += License().apply(configuration)
  }

  /**
   * Configures other pom data of a Maven publication with [configuration].
   */
  fun pom(configuration: MavenPom.() -> Unit) {
    pomConfigurations += configuration
  }

  /**
   * The developer data of a Maven publication.
   *
   * [Reference](https://maven.apache.org/pom.html#developers)
   *
   * @see MavenPomDeveloper
   */
  @Marker class Developer {

    /**
     * The unique ID of this developer in the VSC (aka SCM).
     *
     * If this value is `null`, it is searched through the root project. If it is not found, it will not be bind to
     * the publishing.
     */
    var id: String? = null

    /**
     * The name of this developer.
     *
     * If this value is `null`, it is searched through the root project. If it is not found, it will not be bind to
     * the publishing.
     */
    var name: String? = null

    /**
     * The email of this developer.
     *
     * If this value is `null`, it is searched through the root project. If it is not found, it will not be bind to
     * the publishing.
     */
    var email: String? = null

    /**
     * The URL of this developer.
     *
     * If this value is `null`, it is searched through the root project. If it is not found, it will not be bind to
     * the publishing.
     */
    var url: String? = null

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Developer) return false

      if (id != other.id) return false
      if (name != other.name) return false
      if (email != other.email) return false
      if (url != other.url) return false

      return true
    }

    override fun hashCode(): Int {
      var result = id?.hashCode() ?: 0
      result = 31 * result + (name?.hashCode() ?: 0)
      result = 31 * result + (email?.hashCode() ?: 0)
      result = 31 * result + (url?.hashCode() ?: 0)
      return result
    }
  }

  /**
   * The license data of a Maven publication.
   *
   * [Reference](https://maven.apache.org/pom.html#licenses)
   *
   * @see MavenPomLicense
   */
  @Marker class License {

    /**
     * The name of this license.
     *
     * If this value is `null`, it is searched through the root project. If it is not found, it will not be bind to
     * the publishing.
     */
    var name: String? = null

    /**
     * The URL of this license.
     *
     * If this value is `null`, it is searched through the root project. If it is not found, it will not be bind to
     * the publishing.
     */
    var url: String? = null

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is License) return false

      if (name != other.name) return false
      if (url != other.url) return false

      return true
    }

    override fun hashCode(): Int {
      var result = name?.hashCode() ?: 0
      result = 31 * result + (url?.hashCode() ?: 0)
      return result
    }
  }

  // ///////////////////////////////////////////////////////////////////////////////////
  // //                                Internal APIs                                ////
  // ///////////////////////////////////////////////////////////////////////////////////

  internal fun groupIdOrDefault(): String = groupId
    .ifNull { project.findPropertyOrEnv("publication.groupId")?.toString() }
    .ifNull { project.parentPublicationData?.groupId }
    .ifNull { project.group.toString() }

  internal fun artifactIdOrDefault(): String = artifactId
    .ifNull { project.findPropertyOrEnv("publication.artifactId")?.toString() }
    .ifNull { project.parentPublicationData?.artifactId }
    .ifNull { project.name }

  internal fun pluginIdOrDefault(): String = pluginId
    .ifNull { project.findPropertyOrEnv("publication.pluginId")?.toString() }
    .ifNull { project.parentPublicationData?.pluginId }
    .ifNull { "${groupIdOrDefault()}.${artifactIdOrDefault()}" }

  internal fun versionOrDefault(): String = version
    .ifNull { project.findPropertyOrEnv("publication.version")?.toString() }
    .ifNull { project.parentPublicationData?.version }
    .ifNull { project.version.toString() }

  internal fun displayNameOrDefault(): String = displayName
    .ifNull { project.findPropertyOrEnv("publication.displayName")?.toString() }
    .ifNull { project.parentPublicationData?.displayName }
    .ifNull { artifactIdOrDefault().capitalize() }

  internal fun descriptionOrDefault() = description
    .ifNull { project.findPropertyOrEnv("publication.description")?.toString() }
    .ifNull { project.parentPublicationData?.description }
    .ifNull { project.description }

  internal fun urlOrDefault(recursively: Boolean = true): String? = url
    .ifNull { project.findPropertyOrEnv("publication.url")?.toString() }
    .ifNull { project.parentPublicationData?.url }
    .ifNull { if (recursively) vcsOrDefault(recursively = false) else null }

  internal fun vcsOrDefault(recursively: Boolean = true): String? = vcs
    .ifNull { project.findPropertyOrEnv("publication.vcs")?.toString() }
    .ifNull { project.parentPublicationData?.vcs }
    .ifNull { if (recursively) urlOrDefault(recursively = false) else null }

  internal fun tagsOrDefault(): MutableSet<String> = tags.toMutableSet().apply {
    project.findPropertyOrEnv("publication.tags")?.toString()?.split(", ")?.toList()?.let(::addAll)
    // Root project also needs to be added
    project.parentPublicationData?.tagsOrDefault()?.let(::addAll)
  }

  internal fun organizationNameOrDefault(): String? = organizationName
    .ifNull { project.findPropertyOrEnv("publication.organizationName")?.toString() }
    .ifNull { project.parentPublicationData?.organizationName }

  internal fun organizationUrlOrDefault(): String? = organizationUrl
    .ifNull { project.findPropertyOrEnv("publication.organizationUrl")?.toString() }
    .ifNull { project.parentPublicationData?.organizationUrl }

  internal fun developersOrDefault(): MutableSet<Developer> = developers.toMutableSet().apply {
    val ids = project.findPropertyOrEnv("publication.developers.id")?.toString()?.split(", ").orEmpty()
    val names = project.findPropertyOrEnv("publication.developers.name")?.toString()?.split(", ").orEmpty()
    val emails = project.findPropertyOrEnv("publication.developers.email")?.toString()?.split(", ").orEmpty()
    val urls = project.findPropertyOrEnv("publication.developers.url")?.toString()?.split(", ").orEmpty()

    repeat(maxOf(ids, names, emails, urls).size) { index ->
      add(
        Developer().apply {
          ids.getOrNull(index)?.let { id = it }
          names.getOrNull(index)?.let { name = it }
          emails.getOrNull(index)?.let { email = it }
          urls.getOrNull(index)?.let { url = it }
        }
      )
    }

    // Root project also needs to be added
    project.parentPublicationData?.developersOrDefault()?.let(::addAll)
  }

  internal fun licensesOrDefault(): MutableSet<License> = licenses.toMutableSet().apply {
    val names = project.findPropertyOrEnv("publication.licenses.name")?.toString()?.split(", ").orEmpty()
    val urls = project.findPropertyOrEnv("publication.licenses.url")?.toString()?.split(", ").orEmpty()

    repeat(maxOf(names, urls).size) { index ->
      add(
        License().apply {
          names.getOrNull(index)?.let { name = it }
          urls.getOrNull(index)?.let { url = it }
        }
      )
    }

    // Root project also needs to be added
    project.parentPublicationData?.licensesOrDefault()?.let(::addAll)
  }

  @DslMarker internal annotation class Marker
}
