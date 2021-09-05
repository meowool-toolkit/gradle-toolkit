@file:Suppress("SpellCheckingInspection")

package com.meowool.gradle.toolkit.internal.prebuilt

import com.meowool.gradle.toolkit.internal.RemoteDependencies

internal fun RemoteDependencies.prebuiltApache() {
  startsWith(
    "commons-io",
    "commons-logging",

    "org.apache.tika",
    "org.apache.hbase",
    "org.apache.hadoop",
    "org.apache.commons",
    "org.apache.logging.log4j",

    repositories = { mavenCentral() },
  )
}

