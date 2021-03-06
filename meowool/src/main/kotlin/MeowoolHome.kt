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
import com.meowool.sweekt.ifNull
import org.gradle.api.Project
import java.io.File

private const val HomePropertyKey = "meowool.home"
private const val HomeUppercasePropertyKey = "MEOWOOL_HOME"

/**
 * Returns the 'Meowool' home directory.
 *
 * @author 凛 (RinOrz)
 */
val Project.meowoolHomeDir: File?
  get() = findPropertyOrEnv(HomePropertyKey)?.toString()
    .ifNull { findPropertyOrEnv(HomeUppercasePropertyKey)?.toString() }
    ?.let(::File)
    ?.takeIf { it.exists() }
    .also {
      // TODO The logger decides whether to print or not
      // if (it == null) logger.warn(
      //   "You are currently developing a project belonging to the 'Meowool-Organization', in order " +
      //    "to normalize you should define `$HomePropertyKey` or `$HomeUppercasePropertyKey` in the system environment variables first."
      // )
    }
