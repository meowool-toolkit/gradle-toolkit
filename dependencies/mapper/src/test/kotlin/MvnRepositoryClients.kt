import io.kotest.core.TestConfiguration
import io.ktor.client.features.logging.*

internal fun TestConfiguration.createClient() = MvnRepositoryClient(LogLevel.HEADERS).apply {
  afterTest { close() }
}
