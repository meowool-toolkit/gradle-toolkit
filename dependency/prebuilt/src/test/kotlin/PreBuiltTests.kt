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
@file:Suppress("DEPRECATION", "EXPERIMENTAL_IS_NOT_ENABLED")

import com.meowool.gradle.toolkit.internal.DependencyMapperExtensionImpl
import com.meowool.gradle.toolkit.internal.DependencyMapperExtensionImpl.Companion.cacheJson
import com.meowool.gradle.toolkit.internal.InternalGradleToolkitApi
import com.meowool.gradle.toolkit.internal.prebuilt.prebuilt
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import org.gradle.testfixtures.ProjectBuilder

/**
 * @author 凛 (https://github.com/RinOrz)
 */
@InternalGradleToolkitApi
class PreBuiltTests : StringSpec({
  val tempProjectDir = tempdir()
  val project = ProjectBuilder.builder()
    .withProjectDir(tempProjectDir)
    .build()
  "test cache" {
    var json: String?
    DependencyMapperExtensionImpl(project).apply {
      prebuilt(
        libraries = "Deps",
        plugins = "PluginIds"
      )
      mapping()
      json = project.cacheJson.readText()
    }

    DependencyMapperExtensionImpl(project).apply {
      prebuilt(
        libraries = "Deps",
        plugins = "PluginIds"
      )
      json shouldBe toJson()
    }
  }
})
