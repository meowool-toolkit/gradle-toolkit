import com.meowool.gradle.toolkit.internal.client.GoogleMavenClient
import com.meowool.gradle.toolkit.internal.client.GradlePluginClient
import com.meowool.gradle.toolkit.internal.client.MavenCentralClient
import com.meowool.gradle.toolkit.internal.client.MvnRepositoryClient
import io.kotest.core.TestConfiguration
import okhttp3.logging.HttpLoggingInterceptor

internal fun TestConfiguration.createMvnClient(fetchExactly: Boolean = false) =
  MvnRepositoryClient(fetchExactly).apply { afterTest { close() } }

internal fun TestConfiguration.createCentralClient() = MavenCentralClient().apply { afterTest { close() } }

internal fun TestConfiguration.createGoogleClient() = GoogleMavenClient().apply { afterTest { close() } }

internal fun TestConfiguration.createGradlePluginClient() = GradlePluginClient().apply { afterTest { close() } }
