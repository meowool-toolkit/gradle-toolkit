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
@file:Suppress("MemberVisibilityCanBePrivate")

package com.meowool.gradle.toolkit.internal

import com.meowool.gradle.toolkit.GradleToolkitExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

/**
 * The manually set specification configuration of 'Meowool-Organization'.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class MeowoolManualSpec internal constructor() : MeowoolPresetSpec() {

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
   * Whether to use this specification of [binary-compatibility-validator](https://github.com/Kotlin/binary-compatibility-validator).
   *
   * @see enableBinaryCompatibilityValidator
   * @see disableBinaryCompatibilityValidator
   */
  override var enabledBinaryCompatibilityValidator: Boolean = false

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
