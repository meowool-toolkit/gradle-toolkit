@file:Suppress("SpellCheckingInspection")

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

/**
 * Sets the Kotlin explicit api mode of this project.
 *
 * For more details, see [doc](https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors)
 */
fun Project.kotlinExplicitApi(mode: ExplicitApiMode = ExplicitApiMode.Strict) = kotlinOptions {
  addFreeCompilerArgs("-Xexplicit-api=${mode.name}")
}

/**
 * Sets the Kotlin explicit api mode for default variant of this project.
 *
 * For more details, see [doc](https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors)
 */
fun Project.kotlinDefaultVariantExplicitApi(mode: ExplicitApiMode = ExplicitApiMode.Strict) = kotlinCompile {
  onDefaultVariant { addFreeCompilerArgs("-Xexplicit-api=${mode.name}") }
}

/**
 * Sets the Kotlin explicit api mode for test variant of this project.
 *
 * For more details, see [doc](https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors)
 */
fun Project.kotlinTestVariantExplicitApi(mode: ExplicitApiMode = ExplicitApiMode.Strict) = kotlinCompile {
  onTestVariant { addFreeCompilerArgs("-Xexplicit-api=${mode.name}") }
}