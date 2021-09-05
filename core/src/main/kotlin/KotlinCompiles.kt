import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

/**
 * Uses given [configuration] to configure kotlin common compile task of this project.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
fun Project.kotlinCompile(configuration: KotlinCompile<KotlinCommonOptions>.() -> Unit) {
  extensions.findByType<KotlinMultiplatformExtension>()?.targets?.all {
    compilations.all {
      compileKotlinTask.apply(configuration)
    }
  }
  tasks.withType(configuration)
}

/**
 * Uses given [configuration] to configure kotlin jvm compile task of this project.
 */
fun Project.kotlinJvmCompile(configuration: KotlinJvmCompile.() -> Unit) {
  extensions.findByType<KotlinMultiplatformExtension>()?.targets?.all {
    if (this is KotlinJvmTarget) compilations.all {
      compileKotlinTask.apply(configuration)
    }
  }
  tasks.withType(configuration)
}

/**
 * Configures options for kotlin common compile task of this project with the given [configuration] block.
 */
fun Project.kotlinOptions(configuration: KotlinCommonOptions.() -> Unit) {
  kotlinCompile { kotlinOptions(configuration) }
}

/**
 * Configures options for kotlin jvm compile task of this project with the given [configuration] block.
 */
fun Project.kotlinJvmOptions(configuration: KotlinJvmOptions.() -> Unit) {
  kotlinJvmCompile { kotlinJvmOptions(configuration) }
}

/**
 * Configures options for kotlin jvm compile task with the given [configuration] block.
 */
fun KotlinCompile<*>.kotlinJvmOptions(configuration: KotlinJvmOptions.() -> Unit) = kotlinOptions {
  if (this is KotlinJvmOptions) configuration()
}

/**
 * If compile task belongs to the default variant, execute the given [action] block.
 */
inline fun <T: KotlinCompile<*>> T.onDefaultVariant(action: T.() -> Unit) {
  if (name == "compileKotlin") action()
}

/**
 * If compile task belongs to the test variant, execute the given [action] block.
 */
inline fun <T: KotlinCompile<*>> T.onTestVariant(action: T.() -> Unit) {
  if (name == "compileTestKotlin") action()
}
