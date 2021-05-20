@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-kapt` }

@Suppress("GradleDependency")
dependencies {
  implementation(gradleKotlinDsl())
  implementation(kotlin("stdlib"))
  implementation("com.squareup.moshi:moshi:_")
  implementation("com.squareup:kotlinpoet:_")
  implementation("com.squareup.okhttp3:okhttp:_")
  kapt("com.squareup.moshi:moshi-kotlin-codegen:_")
  testImplementation(kotlin("test-junit"))
}