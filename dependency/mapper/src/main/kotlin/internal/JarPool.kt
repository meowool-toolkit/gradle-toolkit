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
package com.meowool.gradle.toolkit.internal

import internal.ConcurrentScope
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * @author 凛 (https://github.com/RinOrz)
 */
internal class JarPool {
  private val libraries = ConcurrentHashMap<String, Jar>()
  private val projects = ConcurrentHashMap<String, Jar>()
  private val plugins = ConcurrentHashMap<String, Jar>()

  fun librariesJar(rootClassName: String): Jar {
    require(projects.containsKey(rootClassName).not()) { "$rootClassName is a mapped class of project dependencies!" }
    require(plugins.containsKey(rootClassName).not()) { "$rootClassName is a mapped class of plugin ids!" }
    return libraries.getOrPut(rootClassName) { Jar(rootClassName) }
  }

  fun projectsJar(rootClassName: String): Jar {
    require(libraries.containsKey(rootClassName).not()) { "$rootClassName is a mapped class of library dependencies!" }
    require(plugins.containsKey(rootClassName).not()) { "$rootClassName is a mapped class of plugin ids!" }
    return projects.getOrPut(rootClassName) { Jar(rootClassName) }
  }

  fun pluginsJar(rootClassName: String): Jar {
    require(libraries.containsKey(rootClassName).not()) { "$rootClassName is a mapped class of library dependencies!" }
    require(projects.containsKey(rootClassName).not()) { "$rootClassName is a mapped class of project dependencies!" }
    return plugins.getOrPut(rootClassName) { Jar(rootClassName) }
  }

  suspend fun ConcurrentScope<*>.mapLibrariesJar(action: suspend (String, Jar) -> File) =
    libraries.mapConcurrently { action(it.key, it.value) }

  suspend fun ConcurrentScope<*>.mapProjectsJar(action: suspend (String, Jar) -> File) =
    projects.mapConcurrently { action(it.key, it.value) }

  suspend fun ConcurrentScope<*>.mapPluginsJar(action: suspend (String, Jar) -> File) =
    plugins.mapConcurrently { action(it.key, it.value) }
}