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
@file:Suppress("MemberVisibilityCanBePrivate", "DEPRECATION", "unused", "RedundantSetter")

package com.meowool.gradle.toolkit.publisher

import be.vbgn.gradle.cidetect.CiInformation
import com.meowool.gradle.toolkit.publisher.internal.maxOf
import com.meowool.gradle.toolkit.publisher.internal.onEachParentPublicationData
import com.meowool.gradle.toolkit.publisher.internal.parentPublicationData
import com.meowool.sweekt.firstCharTitlecase
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
   * By default, it is searched with `publication.groupId` through the `gradle.properties` of the project or the
   * root project, or even system environment variables. If it is not found in these locations, try to use the group
   * of the parent project or use the [Project.getGroup] as group.
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
   */
  var groupId: String
    get() = _groupId
      .ifNull { project.findPropertyOrEnv("publication.groupId")?.toString() }
      .ifNull { project.onEachParentPublicationData { it._groupId } }
      .ifNull { project.group.toString() }
    set(value) {
      _groupId = value
      project.allprojects {
        // The sub-project needs to be evaluated before we change its group id,
        // otherwise, the AGP maybe throw exceptions:
        //   `You must use different identification (either name or group) for each module.`
        afterEvaluate { group = value }
      }
    }

  /**
   * The artifact id of a Maven publication or the suffix of Gradle plugin id.
   *
   * By default, it is searched with `publication.artifactId` through the `gradle.properties` of the project or the
   * root project, or even system environment variables. If it is not found in these locations, use the
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
   */
  var artifactId: String
    get() = _artifactId
      .ifNull { project.findPropertyOrEnv("publication.artifactId")?.toString() }
      .ifNull { project.name }
    set(value) {
      _artifactId = value
    }

  /**
   * The plugin id of a Gradle plugin.
   *
   * By default, it is searched with `publication.pluginId` through the `gradle.properties` of the project or the
   * root project, or even system environment variables. If it is not found in these locations, use
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
   */
  var pluginId: String
    get() = _pluginId
      .ifNull { project.findPropertyOrEnv("publication.pluginId")?.toString() }
      .ifNull { "$groupId.$artifactId" }
    set(value) {
      _pluginId = value
    }

  /**
   * The version of a Maven publication or Gradle plugin.
   *
   * By default, it is searched with `publication.version` through the `gradle.properties` of the project or the root
   * project, or even system environment variables. If it is not found in these locations, try to use the version of
   * the parent project or the version defined by the [Project.setVersion] is used.
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
   */
  var version: String
    get() = _version
      .ifNull { project.findPropertyOrEnv("publication.version")?.toString() }
      .ifNull { project.onEachParentPublicationData { it._version } }
      .ifNull { project.version.toString() }
    set(value) {
      // Only set this version not in the CI environment
      if (ciInformation.isCi.not()) {
        _version = value
        project.allprojects { version = value }
      }
    }

  /**
   * The version of the Maven publication or Gradle plugin in the continuous integrated environment. If it is
   * null, use [version].
   *
   * Usually this is used to publish different versions locally and in the CI environment (e.g. Github Action).
   *
   * @see version
   */
  var versionInCI: String?
    get() = _version
    set(value) {
      // Only set this version in the CI environment
      if (ciInformation.isCi) {
        _version = value
        project.allprojects { version = value!! }
      }
    }

  /**
   * The display name of a Maven publication or Gradle plugin.
   *
   * By default, it is searched with `publication.displayName` through the `gradle.properties` of the
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
   */
  var displayName: String
    get() = _displayName
      .ifNull { project.findPropertyOrEnv("publication.displayName")?.toString() }
      .ifNull { artifactId.split('-').joinToString(" ") { it.firstCharTitlecase() } }
    set(value) {
      _displayName = value
    }

  /**
   * The description of a Maven publication or Gradle plugin.
   *
   * By default, it is searched with `publication.description` through the `gradle.properties` of the
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
   */
  var description: String?
    get() = _description
      .ifNull { project.findPropertyOrEnv("publication.description")?.toString() }
      .ifNull { project.description }
    set(value) {
      _description = value
      project.allprojects { description = value }
    }

  /**
   * The website URL of a Maven publication or Gradle plugin.
   *
   * By default, it is searched with `publication.url` through the `gradle.properties` of the project or the root
   * project, or even system environment variables. If it is not found in these locations, try to use the url of the
   * parent project or the [vcs] as the website URL, if all are `null`, it will not be bind to the publishing.
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
   */
  var url: String?
    get() = _url ?: defaultUrl()
    set(value) {
      _url = value
    }

  /**
   * The VCS (aka SCM) URL of a Maven publication or Gradle plugin.
   *
   * By default, it is searched with `publication.vcs` through the `gradle.properties` of the project or the root
   * project, or even system environment variables. If it is not found in these locations, try to use the vcs of the
   * parent project or the [url] as the vcs URL, if all are `null`, it will not be bind to the publishing.
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
   */
  var vcs: String?
    get() = _vcs ?: defaultVcs()
    set(value) {
      _vcs = value
    }

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
   */
  val tags: MutableSet<String> = mutableSetOf()
    get() = field.apply {
      project.findPropertyOrEnv("publication.tags")?.toString()?.split(", ")?.toList()?.let(::addAll)
      // Root project also needs to be added
      project.parentPublicationData?.tags?.let(::addAll)
    }

  /**
   * The organization name of a Maven publication.
   *
   * By default, it is searched with `publication.organizationName` through the `gradle.properties` of the project or
   * the root project, or even system environment variables. If it is not found in these locations, it will not be
   * bind to the publishing.
   */
  var organizationName: String?
    get() = _organizationName
      .ifNull { project.findPropertyOrEnv("publication.organizationName")?.toString() }
      .ifNull { project.onEachParentPublicationData { it._organizationName } }
    set(value) {
      _organizationName = value
    }

  /**
   * The organization URL of a Maven publication.
   *
   * By default, it is searched with `publication.organizationUrl` through the `gradle.properties` of the project or
   * the root project, or even system environment variables. If it is not found in these locations, it will not be
   * bind to the publishing.
   */
  var organizationUrl: String?
    get() = _organizationUrl
      .ifNull { project.findPropertyOrEnv("publication.organizationUrl")?.toString() }
      .ifNull { project.onEachParentPublicationData { it._organizationUrl } }
    set(value) {
      _organizationUrl = value
    }

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
   */
  val developers: MutableSet<Developer> = mutableSetOf()
    get() = field.apply {
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
      project.parentPublicationData?.developers?.let(::addAll)
    }

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
   */
  val licenses: MutableSet<License> = mutableSetOf()
    get() = field.apply {
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
      project.parentPublicationData?.licenses?.let(::addAll)
    }

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
  @Marker data class Developer(

    /**
     * The unique ID of this developer in the VSC (aka SCM).
     *
     * If this value is `null`, it is searched through the root project. If it is not found, it will not be bind to
     * the publishing.
     */
    var id: String? = null,

    /**
     * The name of this developer.
     *
     * If this value is `null`, it is searched through the root project. If it is not found, it will not be bind to
     * the publishing.
     */
    var name: String? = null,

    /**
     * The email of this developer.
     *
     * If this value is `null`, it is searched through the root project. If it is not found, it will not be bind to
     * the publishing.
     */
    var email: String? = null,

    /**
     * The URL of this developer.
     *
     * If this value is `null`, it is searched through the root project. If it is not found, it will not be bind to
     * the publishing.
     */
    var url: String? = null,
  )

  /**
   * The license data of a Maven publication.
   *
   * [Reference](https://maven.apache.org/pom.html#licenses)
   *
   * @see MavenPomLicense
   */
  @Marker data class License(

    /**
     * The name of this license.
     *
     * If this value is `null`, it is searched through the root project. If it is not found, it will not be bind to
     * the publishing.
     */
    var name: String? = null,

    /**
     * The URL of this license.
     *
     * If this value is `null`, it is searched through the root project. If it is not found, it will not be bind to
     * the publishing.
     */
    var url: String? = null,
  )

  // ///////////////////////////////////////////////////////////////////////////////////
  // //                                Internal APIs                                ////
  // ///////////////////////////////////////////////////////////////////////////////////

  private var _groupId: String? = null
  private var _artifactId: String? = null
  private var _pluginId: String? = null
  private var _version: String? = null
  private var _displayName: String? = null
  private var _description: String? = null
  private var _url: String? = null
  private var _vcs: String? = null
  private var _organizationName: String? = null
  private var _organizationUrl: String? = null
  private val ciInformation = CiInformation.detect(project)

  private fun defaultUrl(orVcs: Boolean = true): String? = project.findPropertyOrEnv("publication.url")?.toString()
    .ifNull { project.onEachParentPublicationData { it._url } }
    .ifNull { if (orVcs) defaultVcs(orUrl = false) else null }

  private fun defaultVcs(orUrl: Boolean = true): String? = project.findPropertyOrEnv("publication.vcs")?.toString()
    .ifNull { project.onEachParentPublicationData { it._vcs } }
    .ifNull { if (orUrl) defaultUrl(orVcs = false) else null }

  @DslMarker internal annotation class Marker
}
