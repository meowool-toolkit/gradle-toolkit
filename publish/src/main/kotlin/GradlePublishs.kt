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
import com.gradle.publish.PluginBundleExtension
import com.gradle.publish.PublishPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugin.devel.PluginDeclaration

/**
 * Creates the maven publishing.
 *
 * @param name The name of gradle plugin to be created.
 * @param id The id of gradle plugin to be created, the project group + project name by default.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.createGradlePlugin(
  implementationClass: String,
  name: String = findPropertyOrEnv("pom.artifact")?.toString() ?: this.name,
  id: String = (findPropertyOrEnv("pom.group")?.toString() ?: this.group.toString()) + "." + name.decapitalize(),
  displayName: String = findPropertyOrEnv("pom.name")?.toString() ?: name.capitalize(),
  description: String? = findPropertyOrEnv("pom.description")?.toString() ?: this.description.toString(),
  version: String = findPropertyOrEnv("pom.version")?.toString() ?: this.version.toString(),
  website: String? = findProperty("pom.url")?.toString(),
  vcsUrl: String? = findProperty("pom.scm.url")?.toString(),
  tags: List<String> = emptyList(),
  configuration: PluginDeclaration.() -> Unit = {},
) {
  if (!plugins.hasPlugin(PublishPlugin::class)) apply<PublishPlugin>()

  extensions.configure<GradlePluginDevelopmentExtension> {
    plugins.create(name)
    configureGradlePlugin(
      name,
      implementationClass,
      id,
      displayName,
      description,
      version,
      website,
      vcsUrl,
      tags,
      configuration
    )
  }
}

/**
 * Configures the maven publishing. (Won't create)
 *
 * @param name The name of gradle plugin to be configured.
 */
fun Project.configureGradlePlugin(
  name: String = findPropertyOrEnv("pom.artifact")?.toString() ?: this.name,
  implementationClass: String? = null,
  id: String? = null,
  displayName: String? = null,
  description: String? = null,
  version: String? = null,
  website: String? = null,
  vcsUrl: String? = null,
  tags: List<String>? = null,
  configuration: PluginDeclaration.() -> Unit = {},
) {
  version?.let(::setVersion)

  extensions.configure<PluginBundleExtension> {
    website?.let(::setWebsite)
    vcsUrl?.let(::setVcsUrl)
    tags?.let(::setTags)
  }

  extensions.configure<GradlePluginDevelopmentExtension> {
    plugins {
      findByName(name)?.apply {
        this.id = id
        this.implementationClass = implementationClass
        this.displayName = displayName
        this.description = description
        configuration()
      }
        ?: error("I want to set the $name plugin, but this plugin has not been created yet, please call `createGradlePlugin`.")
    }
  }
}
