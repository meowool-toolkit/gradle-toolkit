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
package de.fayard.refreshVersions.core.extensions.gradle

import org.gradle.api.artifacts.repositories.AuthenticationSupported
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.internal.artifacts.repositories.AuthenticationSupportedInternal

val AuthenticationSupported.passwordCredentials: PasswordCredentials?
  // TODO: Remove this workaround for newer Gradle versions when the following issue is fixed:
  // https://github.com/gradle/gradle/issues/14694
  get() = runCatching {
    // We use runCatching to avoid crashing the build if the internal APIs change.
    (this as AuthenticationSupportedInternal?)?.configuredCredentials?.orNull as? PasswordCredentials
  }.getOrNull()
