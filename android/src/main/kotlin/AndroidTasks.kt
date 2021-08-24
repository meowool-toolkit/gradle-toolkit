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
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Find the android task by the given [name], support the use of wildcards instead of the
 * build type of variant.
 *
 * For example:
 * ```
 * findAndroidTask("assemble*")
 *   -- result task (assembleDebug / assembleRelease)
 * ```
 *
 * @param ignoreCase ignore the case of task names
 */
fun Project.findAndroidTask(name: String, ignoreCase: Boolean = false): Task? {
  fun Task.matches(variant: BaseVariant) =
    this.name.equals(name.replace("*", variant.buildType.name.capitalize()), ignoreCase)

  extensions.getByName("android").apply {
    return when (this) {
      is AppExtension -> tasks.find { it.matches(applicationVariants.first()) }
      is LibraryExtension -> tasks.find {it.matches(libraryVariants.first()) }
      else -> {
        require(!name.contains("*")) {
          "Unexpected variant, can't find task, please submit this issue to https://github.com/meowool-toolkit/gradle-dsl-x/issues"
        }
        tasks.find { it.name.equals(name, ignoreCase) }
      }
    }
  }
}
