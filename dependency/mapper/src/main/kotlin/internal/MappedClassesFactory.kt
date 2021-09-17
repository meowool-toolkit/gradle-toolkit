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
package com.meowool.gradle.toolkit.internal

import net.bytebuddy.dynamic.DynamicType
import java.util.concurrent.ConcurrentHashMap

/**
 * A factory used to [make] mapped classes.
 *
 * ```
 * // inputs
 * androidx.compose.ui:ui
 * androidx.compose.material:material
 * androidx.ads:identifier
 * google.webkit:core
 *
 * // outputs
 * class androidx {
 *   class compose {
 *     val ui
 *     val material
 *   }
 *   class ads {
 *     val identifier
 *   }
 * }
 * class google {
 *   val webkit
 * }
 * ```
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal class MappedClassesFactory(private val rootClassName: String) {

  private val addedDependencies = mutableListOf<CharSequence>()

  private val classPool = ConcurrentHashMap<String, DynamicType.Builder<*>>().apply {
    put(rootClassName, createClass(rootClassName, isStatic = false))
  }

  fun map(dependency: CharSequence, mapped: CharSequence) {
    val mappedPath = mapped.split('.')
    mappedPath.foldIndexed(rootClassName) { index, parentName, name ->
      val parent = classPool[parentName]!!
      when (index) {
        // Add a field as a dependency
        mappedPath.lastIndex -> {
          // Avoid adding duplicate dependencies
          if (addedDependencies.contains(dependency).not()) {
            var alias = name
            var extra = 0
            while (parent.toTypeDescription().declaredFields.any { it.name == alias }) {
              // If the field name already exists, use the alias
              alias = name + ++extra
            }
            classPool[parentName] = parent.addField(alias, "$dependency")
            addedDependencies.add(dependency)
          }
          parentName
        }
        // Add inner class
        else -> "$parentName$$name".also { fullName ->
          if (classPool.containsKey(fullName).not()) {
            val inner = createClass(fullName, isStatic = true).setParent(parent)
            classPool[fullName] = inner
            classPool[parentName] = parent.addInnerClasses(inner)
          }
        }
      }
    }
  }

  fun make(): UnloadedType = classPool[rootClassName]!!.makeWith(classPool)
}
