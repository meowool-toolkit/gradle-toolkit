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
@file:Suppress("SpellCheckingInspection")

import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.add
import kotlin.reflect.KClass


/**
 * Adds a new extension to this container when the given name does not exist.
 *
 * @param T the public type of the added extension
 * @param name the name of the extension
 * @param extension the extension instance
 *
 * @throws IllegalArgumentException When an extension with the given name already exists.
 *
 * @see ExtensionContainer.add
 */
inline fun <reified T : Any> ExtensionContainer.addIfNotExists(name: String, extension: () -> T) {
  if (this.findByName(name) == null) {
    add(name, extension())
  }
}


/**
 * Adds a new extension to this container when the given name does not exist.
 *
 * @param T the public type of the added extension
 * @param publicType The extension public type
 * @param name the name of the extension
 * @param extension the extension instance
 *
 * @throws IllegalArgumentException When an extension with the given name already exists.
 *
 * @see ExtensionContainer.add
 */
inline fun <reified T : Any> ExtensionContainer.addIfNotExists(publicType: KClass<T>, name: String, extension: () -> T) {
  if (this.findByName(name) == null) {
    add(publicType, name, extension())
  }
}