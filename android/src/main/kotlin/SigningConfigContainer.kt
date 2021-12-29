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
@file:Suppress("SpellCheckingInspection")

import com.android.build.api.dsl.SigningConfig
import org.gradle.api.NamedDomainObjectContainer

/**
 * ```
 * signingConfigs {
 *   debug {
 *     storeFile file("mydebugkey.keystore")
 *     keyAlias "MyDebugKey"
 *     ...
 *   }
 *   release {
 *     storeFile file("myreleasekey.keystore")
 *     keyAlias "MyReleaseKey"
 *     ...
 *   }
 * }
 * ```
 */
val NamedDomainObjectContainer<out SigningConfig>.debug: SigningConfig
  get() = getNamedOrNull("debug") ?: create("debug")
val NamedDomainObjectContainer<out SigningConfig>.release: SigningConfig
  get() = getNamedOrNull("release") ?: create("release")

fun <T : SigningConfig> NamedDomainObjectContainer<T>.debug(
  configuration: T.() -> Unit
): T = if (this.namedOrNull("debug") != null) {
  this.getNamed("debug", configuration)
} else {
  this.create("debug", configuration)
}

fun <T : SigningConfig> NamedDomainObjectContainer<T>.release(
  configuration: T.() -> Unit
) = if (this.namedOrNull("release") != null) {
  this.named("release", configuration)
} else {
  this.create("release", configuration)
}

// // Legacy

val NamedDomainObjectContainer<out com.android.build.gradle.internal.dsl.SigningConfig>.debug: com.android.build.gradle.internal.dsl.SigningConfig
  get() = getNamedOrNull("debug") ?: create("debug")
val NamedDomainObjectContainer<out com.android.build.gradle.internal.dsl.SigningConfig>.release: com.android.build.gradle.internal.dsl.SigningConfig
  get() = getNamedOrNull("release") ?: create("release")

fun <T : com.android.build.gradle.internal.dsl.SigningConfig> NamedDomainObjectContainer<T>.debug(
  configuration: T.() -> Unit
): T = if (this.namedOrNull("debug") != null) {
  this.getNamed("debug", configuration)
} else {
  this.create("debug", configuration)
}

fun <T : com.android.build.gradle.internal.dsl.SigningConfig> NamedDomainObjectContainer<T>.release(
  configuration: T.() -> Unit
): T = if (this.namedOrNull("release") != null) {
  this.getNamed("release", configuration)
} else {
  this.create("release", configuration)
}
