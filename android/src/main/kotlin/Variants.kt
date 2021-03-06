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
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.LibraryVariant
import com.android.build.gradle.api.TestVariant

fun AppExtension.applicationVariants(configuration: ApplicationVariant.() -> Unit) {
  applicationVariants.all(configuration)
}

fun LibraryExtension.libraryVariants(configuration: LibraryVariant.() -> Unit) {
  libraryVariants.all(configuration)
}

fun TestedExtension.testVariants(configuration: TestVariant.() -> Unit) {
  testVariants.all(configuration)
}

fun BaseExtension.variants(configuration: BaseVariant.() -> Unit) {
  (this as? AppExtension)?.applicationVariants(configuration)
    ?: (this as? LibraryExtension)?.libraryVariants(configuration)
    ?: (this as? TestedExtension)?.testVariants(configuration)
}
