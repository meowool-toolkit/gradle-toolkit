@file:Suppress("MemberVisibilityCanBePrivate")

import com.diffplug.gradle.spotless.SpotlessPlugin
import com.meowool.gradle.toolkit.GradleToolkitExtension
import com.meowool.gradle.toolkit.internal.MeowoolManualSpec
import com.meowool.gradle.toolkit.internal.MeowoolPresetSpec
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

/**
 * Simplified [useMeowoolSpec].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Settings.gradleToolkitWithMeowoolSpec(configuration: MeowoolPresetSpec.() -> Unit = {}) = gradleToolkit {
  useMeowoolSpec(configuration)
}

/**
 * Simplified [useMeowoolManualSpec].
 */
fun Settings.gradleToolkitWithMeowoolManualSpec(configuration: MeowoolManualSpec.() -> Unit) = gradleToolkit {
  useMeowoolManualSpec(configuration)
}

/**
 * Simplified [useMeowoolSpec].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.gradleToolkitWithMeowoolSpec(configuration: MeowoolPresetSpec.() -> Unit = {}) {
  require (this == rootProject) { "You can only setup the 'MeowoolSpec' in the settings.gradle(.kts) or build.gradle(.kts) of root project." }
  extensions.getByType<GradleToolkitExtension>().useMeowoolSpec(configuration)
}

/**
 * Simplified [useMeowoolSpec].
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.gradleToolkitWithMeowoolManualSpec(configuration: MeowoolPresetSpec.() -> Unit = {}) {
  require (this == rootProject) { "You can only setup the 'MeowoolManualSpec' in the settings.gradle(.kts) or build.gradle(.kts) of root project." }
  extensions.getByType<GradleToolkitExtension>().useMeowoolManualSpec(configuration)
}

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
  allprojects {
    project.optIn(spec.optIn)
    spec.repositories.invoke(repositories, project)
    if (spec.enabledSpotless) project.apply<SpotlessPlugin>()
  }
  spec.configurations.forEach { it() }
}