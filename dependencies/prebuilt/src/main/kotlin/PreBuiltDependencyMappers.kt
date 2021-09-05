

import com.meowool.gradle.toolkit.DependencyMapperExtension
import com.meowool.gradle.toolkit.GradleToolkitExtension
import com.meowool.gradle.toolkit.internal.prebuilt.prebuilt
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.configure

/**
 * Use the pre-built dependency mapper configuration.
 *
 * @see dependencyMapper
 */
fun Project.dependencyMapperPrebuilt() = extensions.configure<DependencyMapperExtension> { prebuilt() }

/**
 * Use the pre-built dependency mapper configuration.
 *
 * @see dependencyMapper
 */
fun Settings.dependencyMapperPrebuilt() = dependencyMapper { prebuilt() }

/**
 * Use the pre-built dependency mapper configuration.
 *
 * @see dependencyMapper
 */
fun GradleToolkitExtension.dependencyMapperPrebuilt() = dependencyMapper { prebuilt() }