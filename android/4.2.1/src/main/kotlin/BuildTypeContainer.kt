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
import com.android.build.api.dsl.BuildType
import org.gradle.api.NamedDomainObjectContainer

/**
 * ```
 * buildTypes {
 *   release {
 *     minifyEnabled true
 *     proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
 *   }
 *   debug {
 *     applicationIdSuffix ".debug"
 *     debuggable true
 *   }
 * }
 * ```
 */
val NamedDomainObjectContainer<out BuildType>.debug: BuildType get() = getByName("debug")
val NamedDomainObjectContainer<out BuildType>.release: BuildType get() = getByName("release")

fun <T : BuildType> NamedDomainObjectContainer<T>.debug(configuration: T.() -> Unit) {
  named("debug", configuration)
}

fun <T : BuildType> NamedDomainObjectContainer<T>.release(configuration: T.() -> Unit) {
  named("release", configuration)
}