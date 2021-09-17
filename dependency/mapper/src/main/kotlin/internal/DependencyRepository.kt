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
 * @author 凛 (https://github.com/RinOrz)
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
  class MvnRepository(private val fetchExactly: Boolean) : DependencyRepository() {
    @Transient
    override val client: DependencyRepositoryClient = when (fetchExactly) {
      true -> exactly
      else -> default
    }

    override fun closeClient() {
      exactly.close()
      client.close()
    }

    private companion object {
      val default = MvnRepositoryClient(false)
      val exactly = MvnRepositoryClient(true)
    }
  }
}

