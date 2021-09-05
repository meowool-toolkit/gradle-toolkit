@file:Suppress("SpellCheckingInspection")

package com.meowool.gradle.toolkit.internal.prebuilt

import com.meowool.gradle.toolkit.internal.RemoteDependencies

internal fun RemoteDependencies.prebuiltGoogle() {
  startsWith(
    "com.google",
    repositories = { mavenCentral() },
  )
  startsWith(
    "android",
    "androidx",
    "com.android",
    repositories = { google() },
    // Skip deprecated dependencies
    filter = { it.startsWith("com.android.support").not() }
  )
}

