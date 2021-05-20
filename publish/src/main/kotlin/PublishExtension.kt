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
import com.vanniktech.maven.publish.MavenPublishPlugin
import com.vanniktech.maven.publish.MavenPublishPluginExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/**
 * Apply the plugin of [maven publish](https://github.com/vanniktech/gradle-maven-publish-plugin).
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.mavenPublish() {
  if (!plugins.hasPlugin(MavenPublishPlugin::class.java))
    apply<MavenPublishPlugin>()
}

/**
 * Apply the plugin of SO1 [maven publish](https://github.com/vanniktech/gradle-maven-publish-plugin).
 *
 * @see MavenPublishPluginExtension.sonatypeHost
 */
fun Project.mavenPublishSO1() {
  mavenPublish()
  extensions.configure<MavenPublishPluginExtension> {
    sonatypeHost = SonatypeHost.S01
  }
}

/**
 * Add a task to publish all subprojects.
 */
fun Project.publishSubprojects() {
  rootProject.allprojects {
    tasks.register("publishSubprojects") {
      val publishTasks = subprojects
        .mapNotNull { it.tasks.findByName("publish") }
        .toTypedArray()
      dependsOn(*publishTasks)
      mustRunAfter(*publishTasks)
      group = "publishing"
    }
  }
}
