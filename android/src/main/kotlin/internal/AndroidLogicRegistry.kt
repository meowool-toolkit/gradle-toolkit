package com.meowool.gradle.toolkit.android.internal

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.meowool.gradle.toolkit.internal.InternalGradleToolkitApi
import com.meowool.gradle.toolkit.LogicRegistry
import com.meowool.sweekt.cast
import com.meowool.sweekt.safeCast
import org.gradle.api.Project
import java.util.concurrent.ConcurrentHashMap

/**
 * @author 凛 (https://github.com/RinOrz)
 */
@InternalGradleToolkitApi
object AndroidLogicRegistry {
  /** The default key is used for registration and injection of android logic. */
  internal const val DefaultAndroidKey = "default android logic"

  /** The default candidate key is used for registration and injection of android logic. */
  const val DefaultCandidateAndroidKey = "default android candidate logic"

  private fun LogicRegistry.getOrCreateLogicPool(type: String): ConcurrentHashMap<Any, Any> =
    extraLogics.getOrPut("@#$%android??$type+") { ConcurrentHashMap<Any, Any>() }.cast()

  /** [TestedExtension] */
  internal inline fun LogicRegistry.androidCommonLogics(block: ConcurrentHashMap<Any, Any>.() -> Unit) {
    getOrCreateLogicPool("common").apply(block)
  }

  /** [BaseAppModuleExtension] */
  internal inline fun LogicRegistry.androidAppLogics(block: ConcurrentHashMap<Any, Any>.() -> Unit) {
    getOrCreateLogicPool("application").apply(block)
  }

  /** [LibraryExtension] */
  internal inline fun LogicRegistry.androidLibLogics(block: ConcurrentHashMap<Any, Any>.() -> Unit) {
    getOrCreateLogicPool("library").apply(block)
  }

  /** [TestedExtension] */
  internal fun LogicRegistry.getAndroidCommonLogic(key: Any): (TestedExtension.(Project) -> Unit)? =
    getOrCreateLogicPool("common")[key].safeCast()

  /** [BaseAppModuleExtension] */
  internal fun LogicRegistry.getAndroidAppLogic(key: Any): (BaseAppModuleExtension.(Project) -> Unit)? =
    getOrCreateLogicPool("application")[key].safeCast()

  /** [LibraryExtension] */
  internal fun LogicRegistry.getAndroidLibLogic(key: Any): (LibraryExtension.(Project) -> Unit)? =
    getOrCreateLogicPool("library")[key].safeCast()

}