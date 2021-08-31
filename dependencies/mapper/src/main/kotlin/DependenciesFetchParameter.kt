@file:Suppress("EXPERIMENTAL_API_USAGE")

/**
 * Represents a parameter, which is used when fetching dependencies.
 *
 * @param requests The request values used to fetch.
 * @param resultFilter The filter used to filter dependencies after fetched.
 * @author 凛 (https://github.com/RinOrz)
 */
internal class DependenciesFetchParameter(
  val requests: Array<out String>,
  val clientIds: Set<Int>?,
  val resultFilter: (Dependency) -> Boolean,
) {
  override fun toString(): String = buildString {
    appendLine("requests:")
    appendLine(requests.joinToString("\n").prependIndent())
    clientIds?.joinToString()?.also { ids ->
      append("repositories: ")
      appendLine(ids)
    }
  }
}