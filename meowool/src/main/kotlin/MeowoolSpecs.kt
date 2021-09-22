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

import com.diffplug.gradle.spotless.SpotlessPlugin
import com.meowool.gradle.toolkit.GradleToolkitExtension
import com.meowool.gradle.toolkit.internal.MeowoolManualSpec
import com.meowool.gradle.toolkit.internal.MeowoolPresetSpec
import com.meowool.gradle.toolkit.publisher.PublisherPlugin
import me.tylerbwong.gradle.metalava.plugin.MetalavaPlugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

/**
 * Uses the specification of 'Meowool-Organization' and configure with [configuration].
 *
 * Unlike the [useMeowoolManualSpec], all options in this specification has set automatically.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun GradleToolkitExtension.useMeowoolSpec(configuration: MeowoolPresetSpec.() -> Unit = {}) {
  useMeowoolSpecImpl(MeowoolPresetSpec().apply(configuration))
}

/**
 * Uses the manually set specification of 'Meowool-Organization' and configure with [configuration].
 *
 * Unlike the [useMeowoolSpec], all options in this specification need to be set manually.
 */
fun GradleToolkitExtension.useMeowoolManualSpec(configuration: MeowoolManualSpec.() -> Unit) {
  useMeowoolSpecImpl(MeowoolManualSpec().apply(configuration))
}

private fun GradleToolkitExtension.useMeowoolSpecImpl(spec: MeowoolPresetSpec) {
  allprojects(afterEvaluate = false) {
    project.optIn(spec.optIn)
    project.addFreeCompilerArgs(spec.compilerArguments)
    spec.repositories.invoke(repositories, project)
    if (spec.enabledSpotless(project) && projectDir.resolve(".skip-spotless").exists().not())
      project.apply<SpotlessPlugin>()
    if (spec.enabledMetalava(project) && projectDir.resolve(".skip-metalava").exists().not())
      project.apply<MetalavaPlugin>()
    if (spec.enabledPublisher(project) && projectDir.resolve(".skip-publisher").exists().not())
      project.apply<PublisherPlugin>()
  }
  spec.configurations.forEach { it() }
}

/**
 * Simplified [useMeowoolSpec].
 */
fun Settings.gradleToolkitWithMeowoolSpec(
  spec: MeowoolPresetSpec.() -> Unit = {},
  configuration: GradleToolkitExtension.() -> Unit = {}
) = gradleToolkit {
  useMeowoolSpec(spec)
  configuration()
}

/**
 * Simplified [useMeowoolManualSpec].
 */
fun Settings.gradleToolkitWithMeowoolManualSpec(
  spec: MeowoolManualSpec.() -> Unit,
  configuration: GradleToolkitExtension.() -> Unit = {}
) = gradleToolkit {
  useMeowoolManualSpec(spec)
  configuration()
}

/**
 * Simplified [useMeowoolSpec].
 */
fun Project.gradleToolkitWithMeowoolSpec(
  spec: MeowoolPresetSpec.() -> Unit = {},
  configuration: GradleToolkitExtension.() -> Unit = {}
) {
  require(this == rootProject) { "You can only setup the 'MeowoolSpec' in the settings.gradle(.kts) or build.gradle(.kts) of root project." }
  extensions.getByType<GradleToolkitExtension>().apply(configuration).useMeowoolSpec(spec)
}

/**
 * Simplified [useMeowoolManualSpec].
 */
fun Project.gradleToolkitWithMeowoolManualSpec(
  spec: MeowoolManualSpec.() -> Unit,
  configuration: GradleToolkitExtension.() -> Unit = {}
) {
  require(this == rootProject) { "You can only setup the 'MeowoolManualSpec' in the settings.gradle(.kts) or build.gradle(.kts) of root project." }
  extensions.getByType<GradleToolkitExtension>().apply(configuration).useMeowoolManualSpec(spec)
}
