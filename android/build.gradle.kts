plugins { kotlin; `kotlin-dsl` }

publishingData {
  artifact = "gradle-android"
  name = "Android Gradle Toolkit"
  description = "Raise the practicality of android-gradle-plugin (AGP) to a new level."
}

dependencies.api(Libs.Android.Tools.Build.Gradle version "4.2.2")