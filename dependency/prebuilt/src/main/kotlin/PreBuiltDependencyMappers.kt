import com.meowool.gradle.toolkit.DependencyMapperExtension
import com.meowool.gradle.toolkit.GradleToolkitExtension
import com.meowool.gradle.toolkit.LibraryDependencyDeclaration
import com.meowool.gradle.toolkit.PluginDependencyDeclaration
import com.meowool.gradle.toolkit.ProjectDependencyDeclaration
import com.meowool.gradle.toolkit.internal.prebuilt.prebuilt
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.configure

/**
 * Use the pre-built dependency mapper configuration.
 *
 * @see dependencyMapper
 */
fun Project.dependencyMapperPrebuilt(
  librariesName: String = LibraryDependencyDeclaration.DefaultRootClassName,
  projectsName: String = ProjectDependencyDeclaration.DefaultRootClassName,
  pluginsName: String = PluginDependencyDeclaration.DefaultRootClassName,
) = extensions.configure<DependencyMapperExtension> { prebuilt(librariesName, projectsName, pluginsName) }

/**
 * Use the pre-built dependency mapper configuration.
 *
 * @see dependencyMapper
 */
fun Settings.dependencyMapperPrebuilt(
  librariesName: String = LibraryDependencyDeclaration.DefaultRootClassName,
  projectsName: String = ProjectDependencyDeclaration.DefaultRootClassName,
  pluginsName: String = PluginDependencyDeclaration.DefaultRootClassName,
) = dependencyMapper { prebuilt(librariesName, projectsName, pluginsName) }

/**
 * Use the pre-built dependency mapper configuration.
 *
 * @see dependencyMapper
 */
fun GradleToolkitExtension.dependencyMapperPrebuilt(
  librariesName: String = LibraryDependencyDeclaration.DefaultRootClassName,
  projectsName: String = ProjectDependencyDeclaration.DefaultRootClassName,
  pluginsName: String = PluginDependencyDeclaration.DefaultRootClassName,
) = dependencyMapper { prebuilt(librariesName, projectsName, pluginsName) }