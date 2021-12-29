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
@file:Suppress()

import be.vbgn.gradle.cidetect.CiInformation

/**
 * Returns `true` if the current ide is Intellij-IDEA.
 *
 * @author 凛 (RinOrz)
 */
val isIntelliJ: Boolean get() = System.getenv("__CFBundleIdentifier") == "com.jetbrains.intellij"

/**
 * Returns true if it is currently running in a CI environment.
 *
 * @author 凛 (RinOrz)
 */
val isCiEnvironment: Boolean get() = ciEnvironment.isCi

/**
 * Returns the information of CI environment.
 *
 * @author 凛 (RinOrz)
 */
val ciEnvironment: CiInformation get() = CiInformation.detect()
