package com.meowool.gradle.toolkit.internal

import net.bytebuddy.dynamic.DynamicType
import java.util.concurrent.ConcurrentHashMap

/**
 * A factory used to [produce] mapped classes.
 *
 * ```
 * // inputs
 * androidx.compose.ui:ui
 * androidx.compose.material:material
 * androidx.ads:identifier
 * google.webkit:core
 *
 * // outputs
 * class androidx {
 *   class compose {
 *     val ui
 *     val material
 *   }
 *   class ads {
 *     val identifier
 *   }
 * }
 * class google {
 *   val webkit
 * }
 * ```
 *
 * @author 凛 (https://github.com/RinOrz)
 */
internal class MappedClassesFactory private constructor(private val rootClassName: String) {

  private val addedDependencies = mutableListOf<CharSequence>()
  private val classPool = ConcurrentHashMap<String, DynamicType.Builder<*>>().apply {
    put(rootClassName, createClass(rootClassName, isStatic = false))
  }

  fun map(dependency: CharSequence, mappedClass: CharSequence) {
    val validDep = validDependency(dependency)
    val mappedPath = mappedClass.split('.')
    mappedPath.foldIndexed(rootClassName) { index, parentName, name ->
      val parent = classPool[parentName]!!
      when (index) {
        // Add a field as a dependency
        mappedPath.lastIndex -> {
          // Avoid adding duplicate dependencies
          if (addedDependencies.contains(validDep).not()) {
            var alias = name
            var extra = 0
            while (parent.toTypeDescription().declaredFields.any { it.name == alias }) {
              // If the field name already exists, use the alias
              alias = name + ++extra
            }
            classPool[parentName] = parent.addField(alias, "$validDep:_")
            addedDependencies.add(validDep)
          }
          parentName
        }
        // Add inner class
        else -> "$parentName$$name".also { fullName ->
          if (classPool.containsKey(fullName).not()) {
            val inner = createClass(fullName, isStatic = true).setParent(parent)
            classPool[fullName] = inner
            classPool[parentName] = parent.addInnerClasses(inner)
          }
        }
      }
    }
  }

  private fun make(): DynamicType.Unloaded<out Any> = classPool[rootClassName]!!.makeWith(classPool)

  companion object {

    inline fun produce(rootClassName: String = "Libs", block: MappedClassesFactory.() -> Unit) =
      MappedClassesFactory(rootClassName).apply(block).make()

    fun validDependency(notation: CharSequence): CharSequence = notation.trim().also { trimmed ->
      check(trimmed.count { it == ':' } == 1) {
        "`$this` can only has one `:` symbol used to separate `group` and `artifact`, in other words, " +
          "the notation cannot contain the `artifact` version."
      }
      @Suppress("ReplaceSizeZeroCheckWithIsEmpty")
      require(trimmed.count { it.isWhitespace() } == 0) {
        "`$this` cannot be has spaces."
      }
    }
  }
}
