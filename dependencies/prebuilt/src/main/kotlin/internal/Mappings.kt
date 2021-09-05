@file:Suppress("SpellCheckingInspection")

package com.meowool.gradle.toolkit.internal.prebuilt

import com.meowool.gradle.toolkit.DependencyMapperExtension

internal fun DependencyMapperExtension.prebuiltMapped() {
  mapDependencies(
    "com.tfowl.ktor:ktor-jsoup" to "Ktor.Jsoup",
    "com.github.ben-manes.caffeine:caffeine" to "Caffeine",
    "com.github.promeg:tinypinyin" to "TinyPinyin",
    "de.fayard.refreshVersions:refreshVersions" to "RefreshVersions",
    "com.github.donkingliang:ConsecutiveScroller" to "ConsecutiveScroller",
    "org.zeroturnaround:zt-zip" to "ZtZip",
    "com.andkulikov:transitionseverywhere" to "TransitionsEverywhere",
    "in.arunkumarsampath:transition-x" to "TransitionX",
    "com.diffplug.spotless:spotless-plugin-gradle" to "Gradle.Spotless",
    "com.gradle.publish:plugin-publish-plugin" to "Gradle.Publish.Plugin"
  )
}