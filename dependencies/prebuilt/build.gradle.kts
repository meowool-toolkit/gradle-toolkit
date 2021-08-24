@file:Suppress("SpellCheckingInspection")

plugins { kotlin }

dependencies{
  implementation(gradleKotlinDsl())
  apiProjects(":core", ":dependencies:mapper")
  testImplementation(Libs.Kotest.Runner.Junit5)
}

tasks.withType<Test> {
  useJUnitPlatform()
}