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
package de.fayard.refreshVersions.core

import org.gradle.api.Incubating
import java.io.IOException

@Incubating
abstract class DependencyVersionsFetcher protected constructor(
  val moduleId: ModuleId,
  val repoKey: Any
) {

  companion object;

  @Throws(IOException::class, Exception::class)
  abstract suspend fun getAvailableVersionsOrNull(versionFilter: ((Version) -> Boolean)?): SuccessfulResult?

  data class SuccessfulResult(
    val lastUpdateTimestampMillis: Long,
    val availableVersions: List<Version>
  )

  final override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is DependencyVersionsFetcher) return false

    if (moduleId != other.moduleId) return false
    if (repoKey != other.repoKey) return false

    return true
  }

  final override fun hashCode(): Int {
    var result = moduleId.hashCode()
    result = 31 * result + repoKey.hashCode()
    return result
  }
}
