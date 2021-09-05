@file:Suppress("NOTHING_TO_INLINE")

package com.meowool.gradle.toolkit.android.internal

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

internal inline fun Project.hasAndroidPlugin() = extensions.findByName("android") != null

internal inline fun Project.applyKotlinAndroidIfNotExists() {
  if (plugins.hasPlugin("kotlin-android").not()) apply(plugin = "kotlin-android")
}

internal fun Project.requireAndroidPlugin() = require(hasAndroidPlugin()) {
  "Android extension is not found, please apply `android` or `android-library` plugin first."
}

internal fun Project.requireAndroidAppPlugin() = require(extensions.findByName("android") is BaseAppModuleExtension) {
  "This is not an android application project, please make sure to apply the `android` plugin before inject."
}

internal fun Project.requireAndroidLibPlugin() = require(extensions.findByName("android") is LibraryExtension) {
  "This is not an android library project, please make sure to apply the `android-library` plugin before inject."
}

internal fun <T : TestedExtension> Project.android(configuration: T.() -> Unit) {
  requireAndroidPlugin()
  extensions.getByName("android").apply {
    @Suppress("UNCHECKED_CAST")
    this as T
    configuration()
  }
}