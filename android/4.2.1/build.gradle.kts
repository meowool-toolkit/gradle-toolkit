plugins { kotlin; `kotlin-dsl` }

dependencies {
  val vAGP = "4.2.1"

  apiProjects(":core")
  api(Libs.Android.Tools.Build.Gradle version vAGP)
  testImplementation(Libs.Kotlin.Test.Junit)
}