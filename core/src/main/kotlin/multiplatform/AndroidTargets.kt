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

 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
import com.android.build.gradle.BaseExtension
import com.meowool.sweekt.cast
import com.meowool.sweekt.ifNull
import com.meowool.sweekt.safeCast
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget

/**
 * Enable and configure target of android target.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.androidTarget(
  name: String = "android",
  configure: KotlinAndroidTarget.() -> Unit = {},
) = kotlinMultiplatform {
  android(name, configure)
  extensions.findByName("android").cast<BaseExtension>().sourceSets.all {
    if (manifest.srcFile.exists().not()) {
      manifest.srcFile("src/android${name.capitalize()}/AndroidManifest.xml")
    }
  }
}

/**
 * Configure the main source set of android target.
 */
fun KotlinAndroidTarget.main(configure: KotlinSourceSet.() -> Unit) {
  main.apply(configure)
}
/**
 * Configure the main source set of android target.
 */
fun KotlinAndroidTarget.test(configure: KotlinSourceSet.() -> Unit) {
  test.apply(configure)
}

val NamedDomainObjectContainer<KotlinSourceSet>.androidMain: KotlinSourceSet
  get() = get("androidMain")

val Project.androidMainSourceSet: KotlinSourceSet
  get() = mppExtension.sourceSets.androidMain

val KotlinAndroidTarget.main: KotlinSourceSet
  get() = project.androidMainSourceSet

val NamedDomainObjectContainer<KotlinSourceSet>.androidTest: KotlinSourceSet
  get() = get("androidTest")

val Project.androidTestSourceSet: KotlinSourceSet
  get() = mppExtension.sourceSets.androidTest

val KotlinAndroidTarget.test: KotlinSourceSet
  get() = project.androidTestSourceSet
