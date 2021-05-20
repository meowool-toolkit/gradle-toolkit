@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-dsl` }

dependencies {
  api(project(":core"))
  api(project(":android"))
  api(project(":publish"))
  api("com.diffplug.spotless:spotless-plugin-gradle:_")
  testImplementation(kotlin("test-junit"))
}