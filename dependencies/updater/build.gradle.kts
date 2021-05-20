import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `java-test-fixtures`
  `kotlin-dsl`
  idea
}

dependencies {
  implementationOf(
    gradleKotlinDsl(),
    Libs.KotlinX.Coroutines.Core,
    Libs.Square.OkHttp3.OkHttp,
    Libs.Square.OkHttp3.Logging.Interceptor,
    Libs.Square.Moshi.Kotlin,
    "com.google.cloud:google-cloud-storage:_"
  )
  implementation(Libs.Square.Retrofit2.Retrofit) {
    because("It has ready to use HttpException class")
  }

  testImplementationOf(
    Libs.Square.OkHttp3.Logging.Interceptor,
    Libs.Junit.Jupiter,
    Libs.Kotest.Runner.Junit5,
    Libs.Kotlin.Test.Annotations.Common,
    Libs.Kotlin.Test.Junit5,
    platform(notation = "org.junit:junit-bom:_"),
  )

  testFixturesApi(Libs.Square.OkHttp3.OkHttp)
  testFixturesApi(Libs.Square.OkHttp3.Logging.Interceptor)
  testFixturesApi(Libs.KotlinX.Coroutines.Core)
}

kotlin {
  target.compilations.let {
    it.getByName("testFixtures").associateWith(it.getByName("main"))
  }
}

(components["java"] as AdhocComponentWithVariants).let { javaComponent ->
  javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
  javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }
}

val genResourcesDir = buildDir.resolve("generated/refreshVersions/resources")

sourceSets.main {
  resources.srcDir(genResourcesDir.path)
}

idea {
  module.generatedSourceDirs.add(genResourcesDir)
}

val copyVersionFile by tasks.registering {
  val versionFile = file("version.txt")
  val versionFileCopy = genResourcesDir.resolve("version.txt")
  inputs.file(versionFile)
  outputs.file(versionFileCopy)
  doFirst { versionFile.copyTo(versionFileCopy, overwrite = true) }
}

tasks.withType<KotlinCompile> {
  dependsOn(copyVersionFile)
  kotlinOptions {
    jvmTarget = "1.8"
    freeCompilerArgs = freeCompilerArgs + listOf(
      "-Xinline-classes",
      "-Xmulti-platform", // Allow using expect and actual keywords.
      "-Xopt-in=kotlin.RequiresOptIn",
      "-Xopt-in=de.fayard.refreshVersions.core.internal.InternalRefreshVersionsApi",
    )
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}