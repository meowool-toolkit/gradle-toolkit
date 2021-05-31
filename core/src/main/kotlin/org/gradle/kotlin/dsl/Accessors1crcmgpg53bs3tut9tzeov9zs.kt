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


import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider


/**
 * Provides the existing [buildNeeded][org.gradle.api.DefaultTask] task.
 */
val TaskContainer.`buildNeeded`: TaskProvider<org.gradle.api.DefaultTask>
  get() = named<org.gradle.api.DefaultTask>("buildNeeded")
