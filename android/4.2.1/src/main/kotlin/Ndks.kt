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
@file:Suppress("SpellCheckingInspection", "EnumEntryName")

import com.android.build.api.dsl.Ndk
import com.android.build.gradle.BaseExtension

/**
 * List all available ABI for Android NDK.
 *
 * See [ABIs](https://developer.android.com/ndk/guides/abis#sa).
 *
 * @author 凛 (https://github.com/RinOrz)
 */
enum class NdkAbi(val abi: String) {
  Armeabi("armeabi"),
  Armeabi_v7a("armeabi-v7a"),
  Arm64_v8a("arm64-v8a"),
  X86("x86"),
  X86_64("x86_64"),
}

/**
 * Specifies the Application Binary Interfaces (ABI) that Gradle should build outputs for and
 * package with your APK.
 *
 * @see Ndk.abiFilters
 */
fun BaseExtension.abiFilters(vararg abi: NdkAbi) = defaultConfig {
  ndk.setAbiFilters(abi.map { it.abi })
}

/**
 * Specifies the Application Binary Interfaces (ABI) that Gradle should build outputs for and
 * package with your APK.
 *
 * @see Ndk.abiFilters
 */
fun BaseExtension.abiFilters(abis: List<NdkAbi>) = defaultConfig {
  ndk.setAbiFilters(abis.map { it.abi })
}
