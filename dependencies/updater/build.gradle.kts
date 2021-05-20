import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `java-test-fixtures`
  `kotlin-dsl`
  idea
}

dependencies {
  implementation(gradleKotlinDsl())
  implementation(Libs.KotlinX.Coroutines.Core)
  implementation(Libs.Square.OkHttp3.OkHttp)
  implementation(Libs.Square.OkHttp3.Logging.Interceptor)
  implementation(Libs.Square.Retrofit2.Retrofit.toString()) {
    because("It has ready to use HttpException class")
  }
  implementation(Libs.Square.Moshi.Kotlin)
  implementation("com.google.cloud:google-cloud-storage:_")

  testImplementation(Libs.Square.OkHttp3.Logging.Interceptor)
  testImplementation(platform(notation = "org.junit:junit-bom:_"))
  testImplementation(Libs.Junit.Jupiter)
  testImplementation(Libs.Kotest.Runner.Junit5)
  testImplementation(Libs.Kotlin.Test.Annotations.Common)
  testImplementation(Libs.Kotlin.Test.Junit5)

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