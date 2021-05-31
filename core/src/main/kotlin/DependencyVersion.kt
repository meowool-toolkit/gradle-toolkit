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
@file:Suppress("unused")

/**
 * Change the dependency version.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
infix fun CharSequence.version(ver: String): String = split(":").toMutableList().apply {
  removeLast()
  add(ver)
}.joinToString(":")

/**
 * Remove the dependency version.
 */
fun CharSequence.withoutVersion(): String = split(":").toMutableList().apply {
  removeLast()
}.joinToString(":")

/**
 * Returns the group id from dependency.
 */
fun CharSequence.getGroupId(): String = split(":")[0]

/**
 * Returns the artifact id from dependency.
 */
fun CharSequence.getArtifactId(): String = split(":")[1]

/**
 * Returns the version from dependency.
 */
fun CharSequence.getVersion(): String = split(":")[2]
