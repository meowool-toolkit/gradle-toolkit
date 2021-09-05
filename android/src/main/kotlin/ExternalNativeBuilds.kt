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
import com.android.build.api.dsl.Cmake
import com.android.build.api.dsl.ExternalNativeBuild
import com.android.build.api.dsl.NdkBuild
import com.android.build.gradle.TestedExtension
import com.meowool.gradle.toolkit.android.internal.android
import com.meowool.gradle.toolkit.android.internal.requireAndroidPlugin
import org.gradle.api.Project
import java.io.File

/**
 * Encapsulates per-variant configurations for your external ndk-build project, such as the path
 * to your `CMakeLists.txt` build script and build output directory.
 *
 * For more information about the properties you can configure in this block, see [Cmake]
 *
 * @see ExternalNativeBuild.cmake
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.cmake(path: File? = findCMakeBuildScript(), configuration: Cmake.() -> Unit = {}) {
  requireAndroidPlugin()
  android<TestedExtension> {
    externalNativeBuild.cmake {
      configuration()
      path?.let(::path)
    }
  }
}

/**
 * Encapsulates per-variant configurations for your external ndk-build project, such as the path
 * to your `CMakeLists.txt` build script and build output directory.
 *
 * For more information about the properties you can configure in this block, see [Cmake]
 *
 * @see ExternalNativeBuild.cmake
 */
fun Project.cmake(path: String?, configuration: Cmake.() -> Unit = {}) {
  requireAndroidPlugin()
  android<TestedExtension> {
    externalNativeBuild.cmake {
      configuration()
      path?.let(::path)
    }
  }
}

/**
 * Encapsulates per-variant configurations for your external ndk-build project, such as the path
 * to your `Android.mk` build script and build output directory.
 *
 * For more information about the properties you can configure in this block, see [NdkBuild]
 *
 * @see ExternalNativeBuild.ndkBuild
 */
fun Project.ndkBuild(path: File? = findNdkBuildScript(), configuration: NdkBuild.() -> Unit = {}) {
  requireAndroidPlugin()
  android<TestedExtension> {
    externalNativeBuild.ndkBuild {
      configuration()
      path?.let(::path)
    }
  }
}

/**
 * Encapsulates per-variant configurations for your external ndk-build project, such as the path
 * to your `Android.mk` build script and build output directory.
 *
 * For more information about the properties you can configure in this block, see [NdkBuild]
 *
 * @see ExternalNativeBuild.ndkBuild
 */
fun Project.ndkBuild(path: String?, configuration: NdkBuild.() -> Unit = {}) {
  requireAndroidPlugin()
  android<TestedExtension> {
    externalNativeBuild.ndkBuild {
      configuration()
      path?.let(::path)
    }
  }
}

/**
 * Find the 'CMakeLists.txt' file that exists in this project.
 */
fun Project.findCMakeBuildScript(): File? =
  projectDir.walk().find { it.name == "CMakeLists.txt" }

/**
 * Find the 'Android.mk' file that exists in this project.
 */
fun Project.findNdkBuildScript(): File? =
  projectDir.walk().find { it.name == "Android.mk" }
