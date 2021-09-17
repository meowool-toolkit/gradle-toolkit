package com.meowool.gradle.toolkit

import addIfNotExists
import com.meowool.gradle.toolkit.internal.DependencyMapperExtensionImpl
import com.meowool.gradle.toolkit.internal.InternalGradleToolkitApi
import com.meowool.sweekt.cast
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.findByType

/**
 * A plugin that can map dependencies to classes or fields.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class DependencyMapperPlugin : Plugin<Any> {
  override fun apply(target: Any) {
    when (target) {
      is Settings -> target.bootstrap()
      is Project -> target.bootstrap()
    }
  }

  private fun Settings.bootstrap() {
    gradle.rootProject { bootstrap() }
  }

  private fun Project.bootstrap() {
    extensions.addIfNotExists(DependencyMapperExtension::class, "dependencyMapper") {
      DependencyMapperExtensionImpl(this)
    }
    beforeEvaluate {
      extensions.findByType<DependencyMapperExtension>()
        .cast<DependencyMapperExtensionImpl>()
        .mapping()
    }
  }
}