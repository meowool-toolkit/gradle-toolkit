@file:Suppress("SpellCheckingInspection")

plugins { kotlin }

dependencies{
  apiProjects(":core", ":dependencies:mapper")
  implementation(gradleKotlinDsl())
  testImplementationOf(
    gradleTestKit(),
    Libs.Kotest.Runner.Junit5
  )
}