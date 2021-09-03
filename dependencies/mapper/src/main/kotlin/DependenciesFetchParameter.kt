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
  val resultFilter: ((Dependency) -> Boolean)? = null,
) {
  override fun toString(): String = buildString {
    appendLine("requests:")
    appendLine(requests.joinToString("\n").prependIndent())
    clientIds?.joinToString()?.also { ids ->
      append("repositories: ")
      appendLine(ids)
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is DependenciesFetchParameter) return false

    if (!requests.contentEquals(other.requests)) return false
    if (clientIds != other.clientIds) return false
    if (resultFilter != null || other.resultFilter != null) return false

    return true
  }

  override fun hashCode(): Int {
    var result = requests.contentHashCode()
    result = 31 * result + (clientIds?.hashCode() ?: 0)
    return result
  }
}