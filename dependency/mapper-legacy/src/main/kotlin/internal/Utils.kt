@file:Suppress("EXPERIMENTAL_API_USAGE", "SuspendFunctionOnCoroutineScope")
@file:OptIn(ExperimentalTypeInference::class)

package com.meowool.gradle.toolkit.internal

import com.meowool.sweekt.coroutines.contains
import com.meowool.sweekt.select
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.launch
import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy
import net.bytebuddy.jar.asm.Opcodes
import org.jsoup.nodes.Element
import kotlin.experimental.ExperimentalTypeInference

private val byteBuddy = ByteBuddy()

internal fun createClass(name: String, isStatic: Boolean) = byteBuddy
  .subclass(Any::class.java, ConstructorStrategy.Default.NO_CONSTRUCTORS)
  .modifiers(isStatic.select(Opcodes.ACC_STATIC, 0) or Opcodes.ACC_FINAL or Opcodes.ACC_PUBLIC)
  .name(name)

internal fun <T> DynamicType.Builder<T>.addField(name: String, value: String) = this.defineField(
  name,
  String::class.java,
  Opcodes.ACC_STATIC or Opcodes.ACC_FINAL or Opcodes.ACC_PUBLIC
).value(value)

internal fun <T> DynamicType.Builder<T>.setParent(parentClass: DynamicType.Builder<*>) =
  this.innerTypeOf(parentClass.toTypeDescription()).asMemberType()

internal fun <T> DynamicType.Builder<T>.addInnerClasses(vararg innerClasses: DynamicType.Builder<*>) =
  this.declaredTypes(innerClasses.map { it.toTypeDescription() })

internal fun <T> DynamicType.Builder<T>.makeWith(
  classPool: MutableMap<String, DynamicType.Builder<*>>
): DynamicType.Unloaded<T> {
  val innerClasses = this.toTypeDescription().declaredTypes.map {
    classPool[it.name]!!.makeWith(classPool)
  }
  return this.make().include(innerClasses)
}

internal fun Element.href() = attr("href")

internal suspend inline fun <T> Iterable<T>.forEachConcurrently(
  crossinline action: suspend (T) -> Unit
) = coroutineScope { 
  forEach { 
    launch { action(it) } 
  } 
}

internal suspend inline fun <T> Array<T>.forEachConcurrently(
  crossinline action: suspend (T) -> Unit
) = coroutineScope {
  forEach {
    launch { action(it) }
  }
}

internal suspend inline fun <K, V> MutableMap<K, V>.forEachConcurrently(
  crossinline action: suspend (Map.Entry<K, V>) -> Unit
) = coroutineScope {
  forEach {
    launch { action(it) }
  }
}

internal fun <T, R> Flow<T>.flatMapConcurrently(
  concurrency: Int = Int.MAX_VALUE,
  transform: suspend (T) -> Flow<R>
): Flow<R> = flatMapMerge(concurrency, transform)

internal suspend fun <T> Flow<T>.collectConcurrently(action: suspend (T) -> Unit) = 
  coroutineScope { collect { launch { action(it) } } }

internal suspend inline fun <T> ProducerScope<T>.sendAll(crossinline block: suspend () -> Flow<T>?) =
  block()?.collect { launch { send(it) } }

internal suspend inline fun <T> ProducerScope<T>.sendList(crossinline block: suspend () -> List<T>?) =
  block()?.forEach { launch { send(it) } }

// TODO Migration
internal fun String.removeLineBreaks() = replace("\n", "").replace("\r", "").replace("\r\n", "")

// TODO Migration
internal fun <T> Flow<T>?.orEmpty() = this ?: emptyFlow()

// TODO Migration
internal fun String?.isDigit(): Boolean = this?.all { it.isDigit() } == true

/**
 * Returns `true` if at least one element matches the given [element].
 */
suspend fun <T> Flow<T>.contains(element: T): Boolean = contains { it == element }