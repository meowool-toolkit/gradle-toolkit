plugins { kotlin }

dependencies {
  api(project(":dependencies:builtin"))
  api(project(":dependencies:generator"))
  api(project(":dependencies:updater"))
}