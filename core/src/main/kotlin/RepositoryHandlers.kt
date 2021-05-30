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
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven

/**
 * Adds and configures a Maven mirror repository.
 */
fun RepositoryHandler.mavenMirror(
  mirror: MavenMirrors,
  action: MavenArtifactRepository.() -> Unit = {},
) = maven(mirror.url, action)

/**
 * Adds and configures a Sonatype repository.
 */
fun RepositoryHandler.sonatype(action: MavenArtifactRepository.() -> Unit = {}) {
  maven("https://s01.oss.sonatype.org/content/repositories/public", action)
  maven("https://oss.sonatype.org/content/repositories/public", action)
}

/**
 * Adds and configures a Sonatype snapshots repository.
 */
fun RepositoryHandler.sonatypeSnapshots(action: MavenArtifactRepository.() -> Unit = {}) {
  maven("https://s01.oss.sonatype.org/content/repositories/snapshots", action)
  maven("https://oss.sonatype.org/content/repositories/snapshots", action)
}

/**
 * Adds and configures a Jitpack repository.
 */
fun RepositoryHandler.jitpack(action: MavenArtifactRepository.() -> Unit = {}) = maven("https://jitpack.io", action)
