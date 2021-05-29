import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven

/**
 * Adds and configures a Maven mirror repository.
 */
fun RepositoryHandler.mavenMirror(
  mirror: MavenMirrors,
  action: MavenArtifactRepository.() -> Unit = {},
) = maven(mirror.url, action)

/**
 * Adds and configures a Sonatype repository.
 */
fun RepositoryHandler.sonatype(action: MavenArtifactRepository.() -> Unit = {}) {
  maven("https://s01.oss.sonatype.org/content/repositories/public", action)
  maven("https://oss.sonatype.org/content/repositories/public", action)
}

/**
 * Adds and configures a Sonatype snapshots repository.
 */
fun RepositoryHandler.sonatypeSnapshots(action: MavenArtifactRepository.() -> Unit = {}) {
  maven("https://s01.oss.sonatype.org/content/repositories/snapshots", action)
  maven("https://oss.sonatype.org/content/repositories/snapshots", action)
}

/**
 * Adds and configures a Jitpack repository.
 */
fun RepositoryHandler.jitpack(action: MavenArtifactRepository.() -> Unit = {}) = maven("https://jitpack.io", action)