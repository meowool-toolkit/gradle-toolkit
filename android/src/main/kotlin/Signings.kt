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
import com.android.build.api.dsl.SigningConfig
import com.android.build.gradle.BaseExtension
import java.io.File

/**
 * Specific to signing [configuration] in debug mode.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun BaseExtension.debugSigning(configuration: SigningConfig.() -> Unit) {
  signingConfigs { debug(configuration) }
  buildTypes {
    debug { signingConfig = signingConfigs.debug }
  }
}

/**
 * Specific to signing [configuration] in release mode.
 */
fun BaseExtension.releaseSigning(configuration: SigningConfig.() -> Unit) {
  signingConfigs { release(configuration) }
  buildTypes {
    release { signingConfig = signingConfigs.release }
  }
}

/**
 * Used for signing [configuration] in all build types.
 */
fun BaseExtension.signing(configuration: SigningConfig.() -> Unit) {
  val config = "_#all#"

  signingConfigs {
    create(config) {
      getGlobalScope().project.loadSigningConfigPresets(this)
      configuration()
    }
  }
  buildTypes {
    debug { signingConfig = signingConfigs.getByName(config) }
    release { signingConfig = signingConfigs.getByName(config) }
  }
}

/**
 * Load the defined key config from the given [file].
 */
fun SigningConfig.loadKeyProperties(file: File) {
  val properties = file.toPropertiesOrNull()
    ?: error("The key config file of ${file.absolutePath} can't not exist!")

  storeFile = properties.getProperty("key.store.file")?.run {
    // Support the same level of declaration file
    File(this).takeIf { it.exists() } ?: file.resolveSibling(this)
  }?.also { store ->
    require(store.exists()) {
      "The properties in the ${store.absolutePath} file specify the `key.store.file`, " +
        "but the ${store.absolutePath} file does not exist."
    }
  } ?: error("Please define `key.store.file` property as the path of store file.")

  keyAlias = properties.getProperty("key.alias")
    ?: error("Please define `key.alias` property as the path of key alias name.")

  properties.getProperty("key.password")?.also { keyPassword = it; storePassword = it }
    ?: error("Please define `key.password` property as the path of key or and store password.")

  storePassword = properties.getProperty("key.store.password")
    ?: error("Please define `key.alias` property as the path of key store password.")
}

/**
 * Load the defined key config from the given file [path].
 */
fun SigningConfig.loadKeyProperties(path: String) = loadKeyProperties(File(path))
