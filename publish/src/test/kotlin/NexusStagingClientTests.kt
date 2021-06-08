import kotlinx.coroutines.runBlocking
import net.mbonnin.vespene.lib.NexusStagingClient
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import java.io.File
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class NexusStagingClientTests {
  private val client: NexusStagingClient = NexusStagingClient(
    baseUrl = SonatypeRepo(s01 = true).baseUrl + "/service/local/",
    username = "RinOrz",
    password = "Bin24369664246."
  )

  @Test
  fun profiles() = runBlocking {
//    client.getRepositories().none { it.repositoryId == stagingId }
    println(client.getRepositories().joinToString { it.transitioning.toString() })


    assertTrue { client.getProfiles().any { it.id == "com.meowool" } }
  }
}