@file:Suppress("MemberVisibilityCanBePrivate")

package com.meowool.toolkit.gradle

import GradleToolkitExtension
import OpenSourceLicense
import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

/**
 * The manually set specification configuration of 'Meowool-Organization'.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class MeowoolManualSpec : MeowoolPresetSpec() {

  /**
   * The repositories block for the project of this specification.
   */
  override var repositories: RepositoryHandler.(Project) -> Unit = {}

  /**
   * Opt-in classes.
   *
   * For more details, see [Opt-in](https://kotlinlang.org/docs/opt-in-requirements.html)
   */
  override val optIn: MutableList<String> = mutableListOf()

  /**
   * Whether to use this specification spotless.
   *
   * @see enableSpotless
   * @see disableSpotless
   */
  override var enabledSpotless: Boolean = false

  /**
   * The configurations list of the project of this specification.
   */
  override val configurations: MutableList<GradleToolkitExtension.() -> Unit> = mutableListOf()

  /**
   * Use the preset of [MeowoolPresetSpec.repositories].
   */
  fun useRepositoriesPreset() {
    repositories = presetRepositories()
  }

  /**
   * Use the preset of [MeowoolPresetSpec.optIn].
   */
  fun useOptInPreset() = optIn.addAll(presetOptIn())

  /**
   * Use the preset of [MeowoolPresetSpec.presetAndroid].
   */
  fun useAndroidPreset() = configurations.add(presetAndroid())

  /**
   * Use the preset of [MeowoolPresetSpec.presetSpotless].
   */
  fun useSpotlessPreset() = configurations.add(presetSpotless())

  /**
   * Use the preset of [MeowoolPresetSpec.presetPublications].
   */
  fun usePublicationsPreset() = configurations.add(presetPublications())
}