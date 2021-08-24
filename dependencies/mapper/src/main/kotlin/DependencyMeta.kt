@file:Suppress("unused")

/**
 * @param notation the original notation of dependency
 * @see flattenToDepTree
 * @author 凛 (https://github.com/RinOrz)
 */
internal data class DependencyMeta(
  val name: String,
  var notation: String? = null,
  val children: MutableSet<DependencyMeta> = mutableSetOf()
)