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
package me.tylerbwong.gradle.metalava.extension

import me.tylerbwong.gradle.metalava.Documentation
import me.tylerbwong.gradle.metalava.Format
import me.tylerbwong.gradle.metalava.Signature
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

open class MetalavaExtension(val project: Project) {

  private val parentExtension: MetalavaExtension? get() = project.parent?.extensions?.findByType()
  private var _version: String? = null
  private var _javaSourceLevel: JavaVersion? = null
  private var _format: Format? = null
  private var _signature: Signature? = null
  private var _filename: String? = null
  private var _documentation: Documentation? = null
  private var _outputKotlinNulls: Boolean? = null
  private var _outputDefaultValues: Boolean? = null
  private var _includeSignatureVersion: Boolean? = null
  private var _inputKotlinNulls: Boolean? = null
  private var _reportWarningsAsErrors: Boolean? = null
  private var _reportLintsAsErrors: Boolean? = null
  private var _ignoreUnsupportedModules: Boolean? = null
  private var _androidVariantName: String? = null

  /**
   * The version of Metalava to use.
   */
  var version: String
    get() = _version ?: parentExtension?._version ?: "1.0.0-alpha04"
    set(value) {
      _version = value
    }

  /**
   * A custom Metalava JAR location path to use instead of the embedded dependency.
   */
  var metalavaJarPath: String? = null
    get() = field ?: parentExtension?.metalavaJarPath

  /**
   * Sets the source level for Java source files; default is 11.
   */
  var javaSourceLevel: JavaVersion
    get() = _javaSourceLevel ?: parentExtension?._javaSourceLevel ?: JavaVersion.VERSION_11
    set(value) {
      _javaSourceLevel = value
    }

  /**
   * @see Format
   */
  var format: Format
    get() = _format ?: parentExtension?._format ?: Format.V4
    set(value) {
      _format = value
    }

  /**
   * @see Signature
   */
  var signature: Signature
    get() = _signature ?: parentExtension?._signature ?: Signature.API
    set(value) {
      _signature = value
    }

  /**
   * The final descriptor file output name.
   */
  var filename: String
    get() = _filename ?: parentExtension?._filename
      ?: "${project.version.takeUnless { it == "unspecified" } ?: "current"}.api"
    set(value) {
      _filename = value
    }

  /**
   * @see Documentation
   */
  var documentation: Documentation
    get() = _documentation ?: parentExtension?._documentation ?: Documentation.PROTECTED
    set(value) {
      _documentation = value
    }

  /**
   * Controls whether nullness annotations should be formatted as in Kotlin (with "?" for nullable
   * types, "" for non-nullable types, and "!" for unknown. The default is yes.
   */
  var outputKotlinNulls: Boolean?
    get() = _outputKotlinNulls ?: parentExtension?._outputKotlinNulls ?: true
    set(value) {
      _outputKotlinNulls = value
    }

  /**
   * Controls whether default values should be included in signature files. The default is yes.
   */
  var outputDefaultValues: Boolean
    get() = _outputDefaultValues ?: parentExtension?._outputDefaultValues ?: true
    set(value) {
      _outputDefaultValues = value
    }

  /**
   * Whether the signature files should include a comment listing the format version of the
   * signature file.
   */
  var includeSignatureVersion: Boolean
    get() = _includeSignatureVersion ?: parentExtension?._includeSignatureVersion ?: true
    set(value) {
      _includeSignatureVersion = value
    }

  /**
   * Remove the given packages from the API even if they have not been marked with @hide.
   */
  val hiddenPackages: MutableSet<String> = mutableSetOf()
    get() = field.apply { parentExtension?.hiddenPackages?.also(::addAll) }

  /**
   * Treat any elements annotated with the given annotation as hidden.
   */
  val hiddenAnnotations: MutableSet<String> = mutableSetOf()
    get() = field.apply { parentExtension?.hiddenAnnotations?.also(::addAll) }

  /**
   * Whether the signature file being read should be interpreted as having encoded its types using
   * Kotlin style types: a suffix of "?" for nullable types, no suffix for non-nullable types, and
   * "!" for unknown. The default is no.
   */
  var inputKotlinNulls: Boolean
    get() = _inputKotlinNulls ?: parentExtension?._inputKotlinNulls ?: false
    set(value) {
      _inputKotlinNulls = value
    }

  /**
   * Promote all warnings to errors.
   */
  var reportWarningsAsErrors: Boolean
    get() = _reportWarningsAsErrors ?: parentExtension?._reportWarningsAsErrors ?: false
    set(value) {
      _reportWarningsAsErrors = value
    }

  /**
   * Promote all API lint warnings to errors.
   */
  var reportLintsAsErrors: Boolean
    get() = _reportLintsAsErrors ?: parentExtension?._reportLintsAsErrors ?: false
    set(value) {
      _reportLintsAsErrors = value
    }

  /**
   * If the value is `true`, ignore the unsupported module, otherwise the module that does not
   * support will throw an exception.
   */
  var ignoreUnsupportedModules: Boolean
    get() = _ignoreUnsupportedModules ?: parentExtension?._ignoreUnsupportedModules ?: false
    set(value) {
      _ignoreUnsupportedModules = value
    }

  /**
   * For Android modules defines which variant should be used to resolve classpath when Metalava
   * generates or checks API.
   */
  var androidVariantName: String
    get() = _androidVariantName ?: parentExtension?._androidVariantName ?: "debug"
    set(value) {
      _androidVariantName = value
    }

  /**
   * Remove the given packages from the API even if they have not been marked with @hide.
   */
  fun hiddenPackages(vararg packages: String) {
    hiddenPackages += packages
  }

  /**
   * Treat any elements annotated with the given annotation as hidden.
   */
  fun hiddenAnnotations(vararg annotations: String) {
    hiddenAnnotations += annotations
  }
}
