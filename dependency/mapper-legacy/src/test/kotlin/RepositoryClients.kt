import io.kotest.core.TestConfiguration
import io.ktor.client.features.logging.*

internal fun TestConfiguration.createMvnClient(fetchExactly: Boolean = false) =
  MvnRepositoryClient(fetchExactly, LogLevel.HEADERS).apply { afterTest { close() } }

internal fun TestConfiguration.createCentralClient() = MavenCentralClient(LogLevel.HEADERS).apply {
  afterTest { close() }
}

internal fun TestConfiguration.createGoogleClient() = GoogleMavenClient(LogLevel.HEADERS).apply {
  afterTest { close() }
}
