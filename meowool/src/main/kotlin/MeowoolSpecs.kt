@file:Suppress("MemberVisibilityCanBePrivate")

import com.diffplug.gradle.spotless.SpotlessPlugin
import com.meowool.gradle.toolkit.GradleToolkitExtension
import com.meowool.gradle.toolkit.internal.MeowoolManualSpec
import com.meowool.gradle.toolkit.internal.MeowoolPresetSpec
import org.gradle.kotlin.dsl.apply

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