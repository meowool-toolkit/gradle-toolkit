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
package com.meowool.gradle.toolkit.publisher.internal

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.api.BaseVariant
import com.meowool.gradle.toolkit.publisher.PublicationData
import com.meowool.gradle.toolkit.publisher.PublicationExtension
import findPropertyOrEnv
import net.mbonnin.vespene.lib.NexusStagingClient
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.AuthenticationSupported
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.getCredentials
import org.gradle.kotlin.dsl.register

private const val BUILD_LISTENER = "mavenPublish-buildListener"
private const val REPOSITORIES_TO_CLOSE = "mavenPublish-waitForCloseRepositories"

internal val Project.parentPublication: PublicationExtension? get() = parent?.extensions?.findByType()
internal val Project.parentPublicationData: PublicationData? get() = parentPublication?.data

internal val Project.androidExtension get() = extensions.findByName("android") as? BaseExtension
internal val Project.isAndroid get() = androidExtension != null

/**
 * Represents the build listener for the root project.
 */
internal var Project.buildListener: Boolean
  set(value) = rootProject.extra.set(BUILD_LISTENER, value)
  get() = rootProject.extra.properties[BUILD_LISTENER] == true

/**
 * The map to represents the repositories to be closed and its url.
 */
internal val Project.repositoriesToClose: MutableMap<String, MutableSet<String>>
  get() {
    if (rootProject.extra.has(REPOSITORIES_TO_CLOSE).not()) {
      rootProject.extra[REPOSITORIES_TO_CLOSE] = mutableMapOf<String, MutableSet<String>>()
    }
    @Suppress("UNCHECKED_CAST")
    return rootProject.extra[REPOSITORIES_TO_CLOSE] as MutableMap<String, MutableSet<String>>
  }

/**
 * Returns the description of staging repository.
 */
internal val MavenPublication.stagingDescription: String get() = "Staging $groupId:$artifactId:$version"

/**
 * Creates a new nexus staging client.
 */
internal fun Project.createNexusStagingClient(baseUrl: String): NexusStagingClient {
  val credential = extensions.findByType<PublishingExtension>()?.repositories
    ?.filterIsInstance<AuthenticationSupported>()
    ?.map { it.getCredentials(PasswordCredentials::class) }
    ?.firstOrNull()

  return NexusStagingClient(
    baseUrl = "$baseUrl/service/local/",
    username = findPropertyOrEnv("sonatypeUsername")?.toString()
      ?: findPropertyOrEnv("mavenUsername")?.toString()
      ?: credential?.username
      ?: error("To close the nexus repository you must define `sonatypeUsername` in gradle.properties (Gradle Home) to verify your identity."),
    password = findPropertyOrEnv("sonatypePassword")?.toString()
      ?: findPropertyOrEnv("mavenPassword")?.toString()
      ?: credential?.password
      ?: error("To close the nexus repository you must define `sonatypePassword` in gradle.properties (Gradle Home) to verify your identity."),
  )
}

internal fun Project.createSourcesJar(name: String, block: Jar.() -> Unit) = tasks.register<Jar>(name) {
  archiveClassifier.set("sources")
  block()
}.get()

internal fun BaseExtension.configureAllVariants(configuration: (BaseVariant) -> Unit) {
  (this as? AppExtension)?.applicationVariants?.configureEach(configuration)
    ?: (this as? LibraryExtension)?.libraryVariants?.configureEach(configuration)
    ?: (this as? TestedExtension)?.testVariants?.configureEach(configuration)
}

internal val Project.isCompatible: Boolean
  get() = buildFile.exists() && (
    plugins.hasPlugin("kotlin") ||
      plugins.hasPlugin("org.jetbrains.kotlin.jvm") ||
      plugins.hasPlugin("org.jetbrains.kotlin.js") ||
      plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") ||
      plugins.hasPlugin("java-gradle-plugin") ||
      plugins.hasPlugin("com.android.library") ||
      plugins.hasPlugin("java-library") ||
      plugins.hasPlugin("java")
    )

internal fun <T> maxOf(vararg lists: List<T>): List<T> = lists.maxByOrNull { it.size }.orEmpty()
