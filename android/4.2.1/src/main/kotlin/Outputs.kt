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
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.crash.afterEvaluate
import java.io.File

/**
 * Set application output directory
 *
 * @param targetDirectory application files output directory
 * @param overwrite overwrite existing files
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun AppExtension.outputTo(targetDirectory: File, overwrite: Boolean = true) {
  afterEvaluate {
    applicationVariants {
      outputs.configureEach {
        outputFile.copyTo(targetDirectory.resolve(outputFile.name), overwrite)
      }
    }
  }
}

/**
 * Set library output directory
 *
 * @param targetDirectory application files output directory
 * @param overwrite overwrite existing files
 */
fun LibraryExtension.outputTo(targetDirectory: File, overwrite: Boolean = true) {
  afterEvaluate {
    libraryVariants {
      outputs.configureEach {
        outputFile.copyTo(targetDirectory.resolve(outputFile.name), overwrite)
      }
    }
  }
}

/**
 * Set test output directory
 *
 * @param targetDirectory application files output directory
 * @param overwrite overwrite existing files
 */
fun TestedExtension.outputTo(targetDirectory: File, overwrite: Boolean = true) {
  afterEvaluate {
    testVariants {
      outputs.configureEach {
        outputFile.copyTo(targetDirectory.resolve(outputFile.name), overwrite)
      }
    }
  }
}
