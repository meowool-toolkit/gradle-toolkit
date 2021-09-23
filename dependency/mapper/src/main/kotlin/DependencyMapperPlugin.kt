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
package com.meowool.gradle.toolkit

import addIfNotExists
import com.meowool.gradle.toolkit.internal.DependencyMapperExtensionImpl
import com.meowool.gradle.toolkit.internal.DependencyMapperInternal.CacheDir
import com.meowool.sweekt.cast
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.maybeCreate

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
    tasks.maybeCreate<Delete>("dependencyMapperCleanup").apply {
      delete(projectDir.resolve(CacheDir))
    }
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
