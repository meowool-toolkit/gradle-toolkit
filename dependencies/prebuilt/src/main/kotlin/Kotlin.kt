@file:Suppress("SpellCheckingInspection")

internal fun RemoteDependencies.kotlin() {
  startsWith(
    "org.jetbrains.markdown",
    "org.jetbrains.annotations",
    "org.jetbrains.kotlin",
    "org.jetbrains.kotlinx",
    "org.jetbrains.compose",
    "org.jetbrains.dokka",
    "org.jetbrains.exposed",
    "org.jetbrains.kotlin-wrappers",
    "org.jetbrains.intellij",
    "org.jetbrains.anko",
    "org.jetbrains.spek",
    "org.jetbrains.lets-plot",
    "org.jetbrains.skiko",
    "org.jetbrains.teamcity",
    repositories = { mavenCentral() },
    filter = { it.artifact != "kotlinx-serialization-runtime-jsonparser" }
  )
}