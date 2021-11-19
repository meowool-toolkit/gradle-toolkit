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
plugins {
  `kotlin-dsl`
  kotlin("plugin.serialization")
}

publication {
  data {
    artifactId = "toolkit-dependency-mapper"
    displayName = "Dependency Mapper for Gradle Toolkit"
    description = "Map all dependencies to classes and fields for easy calling in gradle scripts."
  }
  pluginClass = "${data.groupId}.toolkit.DependencyMapperPlugin"
}

dependencies.implementationOf(
  Libs.Ktor.Jsoup,
  Libs.Ktor.Client.OkHttp,
  Libs.Ktor.Client.Logging,
  Libs.Ktor.Client.Serialization,
  Libs.KotlinX.Serialization.Json,
  Libs.Square.OkHttp3.Logging.Interceptor,
  Libs.ByteBuddy.Byte.Buddy,
  Libs.Andreinc.Mockneat,
)