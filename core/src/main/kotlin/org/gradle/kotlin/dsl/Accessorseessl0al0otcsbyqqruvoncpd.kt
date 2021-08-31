/*
 * Copyright (c) 2018. The Meowool Organization Open Source Project
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
@file:Suppress(
  "unused",
  "nothing_to_inline",
  "useless_cast",
  "unchecked_cast",
  "extension_shadowed_by_member",
  "redundant_projection",
  "RemoveRedundantBackticks",
  "ObjectPropertyName",
  "deprecation"
)
@file:org.gradle.api.Generated

/* ktlint-disable */

package org.gradle.kotlin.dsl


import org.gradle.api.Action


/**
 * Retrieves the [ext][org.gradle.api.plugins.ExtraPropertiesExtension] extension.
 */
val org.gradle.api.reporting.ReportingExtension.`ext`: org.gradle.api.plugins.ExtraPropertiesExtension get() =
  (this as org.gradle.api.plugins.ExtensionAware).extensions.getByName("ext") as org.gradle.api.plugins.ExtraPropertiesExtension

/**
 * Configures the [ext][org.gradle.api.plugins.ExtraPropertiesExtension] extension.
 */
fun org.gradle.api.reporting.ReportingExtension.`ext`(configure: Action<org.gradle.api.plugins.ExtraPropertiesExtension>): Unit =
  (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("ext", configure)