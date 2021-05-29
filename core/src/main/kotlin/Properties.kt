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
 */
import annotation.InternalGradleDslXApi
import org.gradle.api.Project
import java.io.File
import java.util.*

@InternalGradleDslXApi
fun File.toPropertiesOrNull(): Properties? = when {
  exists() -> Properties().also {
    it.load(bufferedReader())
  }
  else -> null
}

/**
 * Find and return the properties of the `local.properties` file in the project.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.findLocalProperties(): Properties? =
  projectDir.resolve("local.properties").toPropertiesOrNull()

/**
 * Return the properties of the `local.properties` file in the project.
 */
val Project.localProperties: Properties
  get() = findLocalProperties()
    ?: error("There is no `local.properties` file in the project(${projectDir.absolutePath})")

/**
 * Find the property from the touchable position (maybe system env) of the current project and
 * return it value, if it can't find it, return null.
 */
fun Project.findPropertyOrEnv(key: String): Any? = findProperty(key)
  ?: findLocalProperties()?.getProperty(key)
  ?: rootProject.findLocalProperties()?.getProperty(key)
  ?: System.getenv(key)

/**
 * Returns the property value from the touchable position (maybe system env) of the current project,
 * if it can't find it, return [defaultValue].
 */
fun Project.getPropertyOrEnv(key: String, defaultValue: Any): Any =
  findPropertyOrEnv(key) ?: defaultValue
