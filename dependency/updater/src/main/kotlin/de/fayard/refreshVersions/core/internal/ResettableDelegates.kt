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
package de.fayard.refreshVersions.core.internal

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class ResettableDelegates {

  fun reset() {
    associatedDelegates.forEach {
      with(it) { with(null) { reset() } }
    }
  }

  inner class NullableDelegate<T> : Delegate<T?>() {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? = field

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
      field = value
    }
  }

  inner class LateInit<T : Any> : Delegate<T>() {

    val isInitialized: Boolean get() = this.field != null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = field ?: error(
      "Property ${property.name} not initialized yet! " +
        "Has it been used after reset or before init?"
    )

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
      field = value
    }
  }

  inner class Lazy<T : Any>(private val initializer: () -> T) : Delegate<T>() {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
      return field ?: initializer().apply { field = this }
    }
  }

  inner class MutableLazy<T : Any>(private val initializer: () -> T) : Delegate<T>() {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
      return field ?: initializer().apply { field = this }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
      field = value
    }
  }

  abstract inner class Delegate<T> : ReadOnlyProperty<Any?, T> {

    @Suppress("unused") // Discourage use outside this file.
    fun Nothing?.reset() {
      field = null
    }

    @JvmField protected var field: T? = null

    init {
      @Suppress("LeakingThis") // Safe in our case where the references are kept private.
      associatedDelegates.add(this)
    }
  }

  private val associatedDelegates = mutableListOf<Delegate<*>>()
}
