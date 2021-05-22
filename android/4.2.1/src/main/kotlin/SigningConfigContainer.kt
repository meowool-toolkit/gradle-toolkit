/*
 * Copyright (c) $\YEAR. The Meowool Organization Open Source Project
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
  get() = findByName("debug") ?: create("debug")
val NamedDomainObjectContainer<out SigningConfig>.release: SigningConfig
  get() = findByName("release") ?: create("release")

fun <T : SigningConfig> NamedDomainObjectContainer<T>.debug(configuration: T.() -> Unit) {
  named("debug", configuration)
}

fun <T : SigningConfig> NamedDomainObjectContainer<T>.release(configuration: T.() -> Unit) {
  named("release", configuration)
}

// // Legacy

val NamedDomainObjectContainer<out com.android.build.gradle.internal.dsl.SigningConfig>.debug: com.android.build.gradle.internal.dsl.SigningConfig
  get() = findByName("debug") ?: create("debug")
val NamedDomainObjectContainer<out com.android.build.gradle.internal.dsl.SigningConfig>.release: com.android.build.gradle.internal.dsl.SigningConfig
  get() = findByName("release") ?: create("release")

fun <T : com.android.build.gradle.internal.dsl.SigningConfig> NamedDomainObjectContainer<T>.debug(configuration: T.() -> Unit) =
  named("debug", configuration)

fun <T : com.android.build.gradle.internal.dsl.SigningConfig> NamedDomainObjectContainer<T>.release(configuration: T.() -> Unit) =
  named("release", configuration)