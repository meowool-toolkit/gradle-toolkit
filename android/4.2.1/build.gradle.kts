plugins { kotlin; `kotlin-dsl` }

dependencies {
  val vAGP = "4.2.1"

  api(project(":core"))
  api(Libs.Android.Tools.Build.Gradle version vAGP)
  testImplementation(kotlin("test-junit"))
}