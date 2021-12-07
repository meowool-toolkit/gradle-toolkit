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
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
package me.tylerbwong.gradle.metalava

/**
 * Flags to determine which type of signature file to generate.
 */
enum class Signature(private val signature: String) {
  /**
   * Generate a signature descriptor file.
   */
  API("--api"),

  /**
   * Generate a signature descriptor file listing the exact private APIs.
   */
  PRIVATE_API("--private-api"),

  /*
   * Generate a DEX signature descriptor file listing the APIs.
   */
  DEX_API("--dex-api"),

  /**
   * Generate a DEX signature descriptor file listing the exact private APIs.
   */
  PRIVATE_DEX_API("--private-dex-api"),

  /**
   * Generate a DEX signature descriptor along with file and line numbers.
   */
  DEX_API_MAPPING("--dex-api-mapping"),

  /**
   * Generate a signature descriptor file for APIs that have been removed.
   */
  REMOVED_API("--removed-api");

  override fun toString(): String = signature
}
