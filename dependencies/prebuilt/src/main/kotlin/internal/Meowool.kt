@file:Suppress("SpellCheckingInspection")

package com.meowool.gradle.toolkit.internal.prebuilt

import com.meowool.gradle.toolkit.internal.RemoteDependencies

internal fun RemoteDependencies.prebuiltMeowool() {
  startsWith(
    "com.meowool",
    repositories = { mavenCentral() },
    // Skip deprecated dependencies
    filter = { it.startsWith("com.meowool.toolkit:gradle-dsl-x").not() }
  )
}

