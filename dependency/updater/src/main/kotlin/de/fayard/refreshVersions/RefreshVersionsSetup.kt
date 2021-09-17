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
 * 除如果您正在修改此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
@file:JvmName("RefreshVersionsSetup")

package de.fayard.refreshVersions

import de.fayard.refreshVersions.core.extensions.gradle.isBuildSrc
import de.fayard.refreshVersions.core.internal.legacy.LegacyBootstrapMigrator
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.refreshVersions
import java.io.File

/**
 * Boostrap refreshVersions.
 *
 * Supports both Kotlin and Groovy Gradle DSL.
 *
 * // **`settings.gradle.kts`**
 * ```kotlin
 * import de.fayard.refreshVersions.bootstrapRefreshVersions
 *
 * buildscript {
 *     dependencies.classpath("de.fayard.refreshVersions:refreshVersions:VERSION")
 * }
 *
 * settings.bootstrapRefreshVersions()
 * ```
 *
 * // **`settings.gradle`**
 * ```groovy
 * import de.fayard.refreshVersions.RefreshVersionsSetup
 * buildscript {
 *     dependencies.classpath("de.fayard.refreshVersions:refreshVersions:VERSION")
 * }
 *
 * RefreshVersionsSetup.bootstrap(settings)
 * ```
 */
@JvmOverloads
@JvmName("bootstrap")
fun Settings.bootstrapRefreshVersions(
  versionsPropertiesFile: File = rootDir.resolve("versions.properties")
) {
  require(settings.isBuildSrc.not()) {
    "This bootstrap is only for the root project. For buildSrc, please call " +
      "bootstrapRefreshVersionsForBuildSrc() instead (Kotlin DSL)," +
      "or RefreshVersionsSetup.bootstrapForBuildSrc() if you're using Groovy DSL."
  }
  extensions.create<RefreshVersionsExtension>("refreshVersions")
  refreshVersions {
    this.versionsPropertiesFile = versionsPropertiesFile
  }
  apply(plugin = "de.fayard.refreshVersions")
  with(LegacyBootstrapMigrator) { replaceBootstrapWithPluginsDslSetup() }
}

/**
 * **For buildSrc only!**
 *
 * Boostrap refreshVersions.
 *
 * Supports both Kotlin and Groovy Gradle DSL.
 *
 * // **`settings.gradle.kts`**
 * ```kotlin
 * import de.fayard.refreshVersions.bootstrapRefreshVersionsForBuildSrc
 *
 * buildscript {
 *     dependencies.classpath("de.fayard.refreshVersions:refreshVersions:VERSION")
 * }
 *
 * settings.bootstrapRefreshVersionsForBuildSrc()
 * ```
 *
 * // **`settings.gradle`**
 * ```groovy
 * import de.fayard.refreshVersions.RefreshVersionsSetup
 * buildscript {
 *     dependencies.classpath("de.fayard.refreshVersions:refreshVersions:VERSION")
 * }
 *
 * RefreshVersionsSetup.bootstrapForBuildSrc(settings)
 * ```
 */
@JvmName("bootstrapForBuildSrc")
fun Settings.bootstrapRefreshVersionsForBuildSrc() {
  require(isBuildSrc)
  apply(plugin = "de.fayard.refreshVersions")
  with(LegacyBootstrapMigrator) { replaceBootstrapWithPluginsDslSetup() }
}
