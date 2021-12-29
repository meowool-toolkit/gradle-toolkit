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
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
@file:Suppress("CanBeParameter")

package com.meowool.gradle.toolkit.internal

import com.meowool.gradle.toolkit.internal.client.DependencyRepositoryClient
import com.meowool.gradle.toolkit.internal.client.GoogleMavenClient
import com.meowool.gradle.toolkit.internal.client.GradlePluginClient
import com.meowool.gradle.toolkit.internal.client.MavenCentralClient
import com.meowool.gradle.toolkit.internal.client.MvnRepositoryClient
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * @author 凛 (RinOrz)
 */
@Serializable
internal sealed class DependencyRepository {
  abstract val client: DependencyRepositoryClient

  open fun closeClient() = client.close()

  @Serializable
  object MavenCentral : DependencyRepository() {
    @Transient
    override val client: DependencyRepositoryClient = MavenCentralClient()
  }

  @Serializable
  object Google : DependencyRepository() {
    @Transient
    override val client: DependencyRepositoryClient = GoogleMavenClient()
  }

  @Serializable
  object GradlePluginPortal : DependencyRepository() {
    @Transient
    override val client: DependencyRepositoryClient = GradlePluginClient()
  }

  @Serializable
  object MvnRepository : DependencyRepository() {
    @Transient
    override val client: DependencyRepositoryClient = MvnRepositoryClient(false)
  }

  @Serializable
  object MvnExactlyRepository : DependencyRepository() {
    @Transient
    override val client: DependencyRepositoryClient = MvnRepositoryClient(true)
  }
}
