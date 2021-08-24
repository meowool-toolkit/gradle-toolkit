@file:Suppress("SpellCheckingInspection")

plugins { kotlin }

dependencies{
  implementation(gradleKotlinDsl())
  apiProjects(":core", ":dependencies:mapper")
}