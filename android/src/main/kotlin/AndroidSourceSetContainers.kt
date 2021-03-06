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
@file:Suppress("DEPRECATION", "UnstableApiUsage")

import com.android.build.api.dsl.AndroidSourceSet
import org.gradle.api.NamedDomainObjectContainer

/**
 * ```
 * sourceSets {
 *   main {
 *     java.srcFiles("main/kotlin")
 *     manifest.srcFiles("main/AndroidManifest.xml")
 *   }
 *   test {
 *     java.srcFiles("test/kotlin")
 *   }
 * }
 * ```
 */
val NamedDomainObjectContainer<out AndroidSourceSet>.main: AndroidSourceSet get() = getNamed("main")
val NamedDomainObjectContainer<out AndroidSourceSet>.test: AndroidSourceSet get() = getNamed("test")
val NamedDomainObjectContainer<out AndroidSourceSet>.androidTest: AndroidSourceSet
  get() = getNamed("androidTest")

fun <T : AndroidSourceSet> NamedDomainObjectContainer<T>.main(configuration: T.() -> Unit) {
  if (this.any { it.name == "main" }) {
    this.getNamed("main", configuration)
  } else {
    this.create("main", configuration)
  }
}

fun <T : AndroidSourceSet> NamedDomainObjectContainer<T>.test(configuration: T.() -> Unit) {
  if (this.any { it.name == "test" }) {
    this.getNamed("test", configuration)
  } else {
    this.create("test", configuration)
  }
}

fun <T : AndroidSourceSet> NamedDomainObjectContainer<T>.androidTest(configuration: T.() -> Unit) {
  if (this.any { it.name == "androidTest" }) {
    this.getNamed("androidTest", configuration)
  } else {
    this.create("androidTest", configuration)
  }
}

// // Legacy

val NamedDomainObjectContainer<out com.android.build.gradle.api.AndroidSourceSet>.main: com.android.build.gradle.api.AndroidSourceSet
  get() = getNamed("main")
val NamedDomainObjectContainer<out com.android.build.gradle.api.AndroidSourceSet>.test: com.android.build.gradle.api.AndroidSourceSet
  get() = getNamed("test")
val NamedDomainObjectContainer<out com.android.build.gradle.api.AndroidSourceSet>.androidTest: com.android.build.gradle.api.AndroidSourceSet
  get() = getNamed("androidTest")

fun <T : com.android.build.gradle.api.AndroidSourceSet> NamedDomainObjectContainer<T>.main(
  configuration: T.() -> Unit
): T = if (this.namedOrNull("main") != null) {
  this.getNamed("main", configuration)
} else {
  this.create("main", configuration)
}

fun <T : com.android.build.gradle.api.AndroidSourceSet> NamedDomainObjectContainer<T>.test(
  configuration: T.() -> Unit
): T = if (this.namedOrNull("test") != null) {
  this.getNamed("test", configuration)
} else {
  this.create("test", configuration)
}

fun <T : com.android.build.gradle.api.AndroidSourceSet> NamedDomainObjectContainer<T>.androidTest(
  configuration: T.() -> Unit
): T = if (this.namedOrNull("androidTest") != null) {
  this.getNamed("androidTest", configuration)
} else {
  this.create("androidTest", configuration)
}
