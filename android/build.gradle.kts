plugins { kotlin; `kotlin-dsl` }

dependencies {
  apiProjects(":core")
  api(Libs.Android.Tools.Build.Gradle version "4.2.1")
  testImplementation(Libs.Kotlin.Test.Junit)
}