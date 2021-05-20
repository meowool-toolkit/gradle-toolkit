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
@file:Suppress("PackageDirectoryMismatch", "SpellCheckingInspection", "unused")

import dependencies.DependencyNotationAndGroup
import org.gradle.api.Incubating

/**
 * COIL stands for **Co**routine **I**mage **L**oader.
 *
 * It's a lightweight Android library made at Instacart.
 *
 * [Official website](https://coil-kt.github.io/coil/)
 *
 * GitHub Page: [coil-kt/coil](https://github.com/coil-kt/coil/)
 */
@Incubating
internal object COIL : DependencyNotationAndGroup(group = "io.coil-kt", name = "coil") {

  @JvmField val base = "$artifactPrefix-base:_"
  @JvmField val gif = "$artifactPrefix-gif:_"
  @JvmField val svg = "$artifactPrefix-svg:_"
  @JvmField val video = "$artifactPrefix-video:_"
}
