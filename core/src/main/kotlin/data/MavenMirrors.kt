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
@file:Suppress("SpellCheckingInspection")

/**
 * Mirrors of maven repositories
 *
 * @author 凛 (https://github.com/RinOrz)
 */
sealed class MavenMirrors(val url: String) {

  /* More see https://maven.aliyun.com/repository */
  sealed class Aliyun(type: String) : MavenMirrors("https://maven.aliyun.com/repository/$type") {
    object Google : Aliyun("google")
    object Public : Aliyun("public")
    object Spring : Aliyun("spring")
    object Central : Aliyun("central")
    object JCenter : Aliyun("jcenter")
    object GrailsCore : Aliyun("grails-core")
    object SpringPlugin : Aliyun("spring-plugin")
    object GradlePlugin : Aliyun("gradle-plugin")
    object ApacheSnapshots : Aliyun("apache-snapshots")
  }

  /* More see https://mirrors.huaweicloud.com/ */
  object Huawei : MavenMirrors("https://repo.huaweicloud.com/repository/maven/")

  /* More see https://mirrors.cloud.tencent.com/ */
  object Tencent : MavenMirrors("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
}
