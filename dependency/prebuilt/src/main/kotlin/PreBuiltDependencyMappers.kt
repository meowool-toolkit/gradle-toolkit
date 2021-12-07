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
import com.meowool.gradle.toolkit.DependencyMapperExtension
import com.meowool.gradle.toolkit.GradleToolkitExtension
import com.meowool.gradle.toolkit.LibraryDependencyDeclaration
import com.meowool.gradle.toolkit.PluginDependencyDeclaration
import com.meowool.gradle.toolkit.ProjectDependencyDeclaration
import com.meowool.gradle.toolkit.internal.prebuilt.prebuilt
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.configure

/**
 * Use the pre-built dependency mapper configuration.
 *
 * @see dependencyMapper
 */
fun Project.dependencyMapperPrebuilt(
  librariesName: String = LibraryDependencyDeclaration.DefaultRootClassName,
  projectsName: String = ProjectDependencyDeclaration.DefaultRootClassName,
  pluginsName: String = PluginDependencyDeclaration.DefaultRootClassName,
) = extensions.configure<DependencyMapperExtension> { prebuilt(librariesName, projectsName, pluginsName) }

/**
 * Use the pre-built dependency mapper configuration.
 *
 * @see dependencyMapper
 */
fun Settings.dependencyMapperPrebuilt(
  librariesName: String = LibraryDependencyDeclaration.DefaultRootClassName,
  projectsName: String = ProjectDependencyDeclaration.DefaultRootClassName,
  pluginsName: String = PluginDependencyDeclaration.DefaultRootClassName,
) = dependencyMapper { prebuilt(librariesName, projectsName, pluginsName) }

/**
 * Use the pre-built dependency mapper configuration.
 *
 * @see dependencyMapper
 */
fun GradleToolkitExtension.dependencyMapperPrebuilt(
  librariesName: String = LibraryDependencyDeclaration.DefaultRootClassName,
  projectsName: String = ProjectDependencyDeclaration.DefaultRootClassName,
  pluginsName: String = PluginDependencyDeclaration.DefaultRootClassName,
) = dependencyMapper { prebuilt(librariesName, projectsName, pluginsName) }
