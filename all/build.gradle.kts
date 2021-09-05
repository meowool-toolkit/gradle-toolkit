plugins { kotlin; `kotlin-dsl` }
//
//afterEvaluate {
//  apply<MavenPublishPlugin>()
//  apply<org.jetbrains.dokka.gradle.DokkaPlugin>()
//  apply<com.gradle.publish.PublishPlugin>()
//  extensions.configure<GradlePluginDevelopmentExtension> {
//    plugins.create("toolkit") {
//      implementationClass = "$group.toolkit.GradleToolkitPlugin"
//      id = "com.meowool.gradle.toolkit"
//      displayName = "Toolkit"
//    }
//  }
//  afterEvaluate {
//    publishing {
//      publications.create<MavenPublication>("maven") {
//        components.findByName("java")?.let(::from)
//      }
//
//      publications.withType<MavenPublication> {
//        println("groupId = $groupId, artifactId = $artifactId, $name")
//        groupId = "custom.group"
//        version = "1.0"
//        if (this@withType.name.endsWith("PluginMarkerMaven").not()) {
//          // There will be a suffix when a multi-platform project, so use the way of prefix replacing.
//          // E.g. library, library-jvm, library-native
//          artifactId = "newtoolkit" + artifactId.removePrefix(project.name)
//        }
//      }
//    }
//  }
//}
publication.pluginClass = "$group.toolkit.GradleToolkitPlugin"

//apply<com.gradle.publish.PublishPlugin>()

//group = "com.GROUP"
//version = "1.0.0"
//
//
//gradlePlugin {
//  plugins {
//    create("hello") {
//      id = "com.example.hello"
//      implementationClass = "Missing implementationClass for hello"
//    }
//  }
//}
//publishing {
//
//  publications.create<MavenPublication>("maven") {
//    groupId = "org.gradle.sample"
//    artifactId = "Checking"
//  }
//}
//afterEvaluate {
//  publishing {
//    publications.forEach {
//      println("name = ${this.artifactId}, ${this.groupId}")
//    }
//}
//
//}
dependencies.apiOf(
  projects.android,
  projects.meowool,
  projects.publisher,
  projects.dependencies,
)