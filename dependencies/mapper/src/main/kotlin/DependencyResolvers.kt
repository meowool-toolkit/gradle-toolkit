@file:Suppress("SpellCheckingInspection")

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNot

/**
 * Convert all defined dependencies to metas and flattens.
 *
 * ```
 * // inputs
 * com.compose.ui:ui
 * com.compose.material:material
 * com.ads:identifier
 * google.webkit:core
 *
 * // outputs
 * com {
 *   compose {
 *     ui
 *     material
 *   }
 *   ads
 * }
 * google {
 *   webkit
 * }
 * ```
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal suspend fun Flow<String>.flattenToDepTree(
  formatter: DepFormatter = DepFormatter.Default,
): Collection<DependencyMeta> {
  val record = mutableMapOf<String, DependencyMeta>()

  this.filterNot { it.isBlank() }.collect {
    val raw = it.validDepNotation()
    val formatted = formatter.format(raw)

    record.addDependency(raw, formatted)
  }

  return record.filterKeys { '.' !in it }.values
}

/**
 * Convert the given [path] of dependency group and artifact to dependency meta and add it's to this map.
 *
 * @param raw the raw notation of the dependency, see [DependencyMeta.notation]
 * @param path if the value has `.`, the [DependencyMeta.notation] will be `null`
 * @param parent the parent path of specified [path], if exists
 */
private fun MutableMap<String, DependencyMeta>.addDependency(
  raw: String,
  path: String,
  parent: String? = null,
) {
  val name = path.substringBefore('.')
  // Itself path, excluding its sub-paths
  val selfPath = parent?.let { "$it." }.orEmpty() + name
  val self = this.getOrPut(selfPath) {
    DependencyMeta(name).also {
      // Add this meta to the corresponding parent meta
      parent?.let(this::get)?.children?.add(it)
    }
  }

  // Recursively: foo.bar.baz -> bar.baz -> baz
  path.substringAfter('.', missingDelimiterValue = "")
    .takeIf { it.isNotEmpty() }
    ?.also { subpath -> addDependency(raw, subpath, parent = selfPath) }

  // No subpaths
    ?: let {
      self.notation = raw
    }
}

internal fun String.validDepNotation(): String = this.trim().also { notation ->
  check(notation.count { it == ':' } == 1) {
    "`$this` can only has one `:` symbol used to separate `group` and `artifact`, in other words, " +
      "the notation cannot contain the `artifact` version."
  }
  @Suppress("ReplaceSizeZeroCheckWithIsEmpty")
  require(notation.count { it.isWhitespace() } == 0) {
    "`$this` cannot be has spaces."
  }
}