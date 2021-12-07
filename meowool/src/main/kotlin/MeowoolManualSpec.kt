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
  override val optIn: MutableSet<String> = mutableSetOf()

  /**
   * Kotlin compiler arguments.
   *
   * For more details, see [Kotlin Source](https://github.com/JetBrains/kotlin/blob/master/compiler/cli/cli-common/src/org/jetbrains/kotlin/cli/common/arguments/CommonCompilerArguments.kt)
   */
  override val compilerArguments: MutableSet<String> = mutableSetOf()

  /**
   * The project whether to use this specification spotless.
   *
   * If the [Project.getProjectDir] contains a file named `.skip-spotless`, the project will not enable spotless
   * plugin by 'Meowool-Spec' anyway.
   *
   * @see enableSpotless
   * @see disableSpotless
   */
  override var enabledSpotless: (Project) -> Boolean = { false }

  /**
   * The project whether to use this specification of metalava.
   *
   * If the [Project.getProjectDir] contains a file named `.skip-metalava`, the project will not enable metalava
   * plugin by 'Meowool-Spec' anyway.
   *
   * @see enableMetalava
   * @see disableMetalava
   */
  override var enabledMetalava: (Project) -> Boolean = { false }

  /**
   * The project whether to use this specification of binary-compatibility-validator.
   *
   * If the [Project.getProjectDir] contains a file named `.skip-bcv`, the project will not enable
   * binary-compatibility-validator plugin by 'Meowool-Spec' anyway.
   *
   * @see enableBinaryCompatibilityValidator
   * @see disableBinaryCompatibilityValidator
   */
  override var enabledBinaryCompatibilityValidator: (Project) -> Boolean = { false }

  /**
   * The project whether to use this specification of publisher.
   *
   * If the [Project.getProjectDir] contains a file named `.skip-publisher`, the project will not enable publisher
   * plugin by 'Meowool-Spec' anyway.
   *
   * @see enablePublisher
   * @see disablePublisher
   */
  override var enabledPublisher: (Project) -> Boolean = { false }

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
   * Use the preset of [MeowoolPresetSpec.compilerArguments].
   */
  fun useCompilerArgumentsPreset() = compilerArguments.addAll(presetCompilerArguments())

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
