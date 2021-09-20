/*
 * Copyright (c) 2021. The Meowool Organization Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
@file:Suppress("EXPERIMENTAL_API_USAGE")
@file:OptIn(ExperimentalTypeInference::class)

package com.meowool.gradle.toolkit.internal

import com.meowool.sweekt.Hosting
import com.meowool.sweekt.className
import com.meowool.sweekt.coroutines.contains
import com.meowool.sweekt.hosting
import com.meowool.sweekt.isEnglish
import com.meowool.sweekt.isEnglishNotPunctuation
import com.meowool.sweekt.iteration.endsWith
import com.meowool.sweekt.iteration.isNullOrEmpty
import com.meowool.sweekt.iteration.size
import com.meowool.sweekt.iteration.startsWith
import com.meowool.sweekt.select
import internal.ConcurrentScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.retry
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy
import net.bytebuddy.jar.asm.Opcodes
import org.jsoup.nodes.Element
import java.io.File
import kotlin.experimental.ExperimentalTypeInference

@PublishedApi
internal val DefaultJson = Json {
  this.ignoreUnknownKeys = true
  this.prettyPrint = true
  this.prettyPrintIndent = "  "
  this.serializersModule = SerializersModule {
    polymorphic(DependencyCollector::class) {
      subclass(LibraryDependencyDeclarationImpl.Data::class)
      subclass(ProjectDependencyDeclarationImpl.Data::class)
      subclass(PluginDependencyDeclarationImpl.Data::class)
    }
    contextual(
      File::class,
      object : KSerializer<File> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(File::class.className, PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: File) = encoder.encodeString(value.absolutePath)
        override fun deserialize(decoder: Decoder): File = File(decoder.decodeString())
      }
    )
  }
}

internal typealias UnloadedType = DynamicType.Unloaded<*>

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
): UnloadedType {
  val innerClasses = this.toTypeDescription().declaredTypes.map {
    classPool[it.name]!!.makeWith(classPool)
  }
  return this.make().include(innerClasses)
}

internal fun <T> hosting(
  key: Any? = null,
  initializer: () -> T,
): Hosting<T> = hosting(key, lock = null, initializer = initializer)

internal fun Element.href() = attr("href")

@Suppress("SuspendFunctionOnCoroutineScope")
internal suspend fun CoroutineScope.consumeChannel(block: suspend ConcurrentScope<Any>.() -> Unit) =
  produce { block(ConcurrentScope(this)) }.consumeEach {  }

internal fun <E> concurrentFlow(@BuilderInference block: suspend ConcurrentScope<E>.() -> Unit): Flow<E> =
  channelFlow { block(ConcurrentScope(this)) }

internal fun <T, R> Flow<T>.flatMapConcurrently(
  concurrency: Int = Int.MAX_VALUE,
  transform: suspend (T) -> Flow<R>
): Flow<R> = flatMapMerge(concurrency, transform)

/**
 * Returns `this` value if it satisfies the given [predicate] or `null`, if it doesn't.
 *
 * For detailed usage information see the documentation for [scope functions](https://kotlinlang.org/docs/reference/scope-functions.html#takeif-and-takeunless).
 */
// TODO Migration
internal inline fun <T> List<T>.takeIfEmpty(): List<T>? {
  return if (isEmpty()) this else null
}

// TODO Migration
internal inline fun <T> List<T>?.takeIfNullOrEmpty(): List<T>? {
  return if (isNullOrEmpty()) this else null
}

/**
 * Returns `this` value if it satisfies the given [predicate] or `null`, if it doesn't.
 *
 * For detailed usage information see the documentation for [scope functions](https://kotlinlang.org/docs/reference/scope-functions.html#takeif-and-takeunless).
 */
// TODO Migration
internal inline fun <T> List<T>?.takeIfNotEmpty(): List<T>? {
  return if (isNullOrEmpty()) null else this
}

// TODO Migration
internal inline fun <C: CharSequence> C?.takeIfNotEmpty(): C? {
  return if (isNullOrEmpty()) null else this
}
// TODO Migration
internal inline fun <C: CharSequence> C?.takeIfNullOrEmpty(): C? {
  return if (isNullOrEmpty()) this else null
}
// TODO Migration
internal inline fun <C: CharSequence> C.takeIfEmpty(): C? {
  return if (isEmpty()) this else null
}

// TODO Migration
internal inline fun String?.takeIfNotEmpty(): String? {
  return if (isNullOrEmpty()) null else this
}
// TODO Migration
internal inline fun String?.takeIfNullOrEmpty(): String? {
  return if (isNullOrEmpty()) this else null
}
// TODO Migration
internal inline fun String.takeIfEmpty(): String? {
  return if (isEmpty()) this else null
}

// TODO Migration
internal fun String.removeLineBreaks() = replace("\n", "").replace("\r", "").replace("\r\n", "")

// TODO Migration
internal fun <T> Flow<T>?.orEmpty() = this ?: emptyFlow()

// TODO Migration
internal fun String?.isDigits(): Boolean = this?.all { it.isDigit() } == true

/**
 * Returns `true` if all contents of this char sequence is chinese.
 */
fun CharSequence.isEnglish(allowPunctuation: Boolean = true): Boolean = all {
  if (allowPunctuation) it.isEnglish() else it.isEnglishNotPunctuation()
}

internal fun <T> List<T>.dropFirst(): List<T> = drop(1)
internal fun <T> List<T>.dropLast(): List<T> = dropLast(1)

internal fun <T> MutableList<T>.deleteLast(n: Int) {
  subList(this.size - n, this.size).clear()
}

internal fun <T> MutableList<T>.delete(n: Int) {
  subList(0, n).clear()
}

/**
 * Returns `true` if this iterable starts with given [slice].
 */
fun <T> Iterable<T>.startsWith(slice: T) = this.first() == slice

/**
 * Returns `true` if this iterable ends with given [slice].
 */
fun <T> Iterable<T>.endsWith(slice: T) = this.last() == slice

internal fun <T> List<T>.dropPrefix(prefix: T): List<T> =
  if (startsWith(prefix)) this.dropFirst() else this

internal fun <T> List<T>.dropPrefix(vararg prefix: T): List<T> =
  if (startsWith(*prefix)) this.drop(prefix.size) else this

internal fun <T> List<T>.dropPrefix(prefix: Iterable<T>): List<T> =
  if (startsWith(prefix)) this.drop(prefix.size) else this

internal fun <T> List<T>.dropSuffix(suffix: T): List<T> =
  if (endsWith(suffix)) this.dropLast() else this

internal fun <T> List<T>.dropSuffix(vararg suffix: T): List<T> =
  if (endsWith(*suffix)) this.dropLast(suffix.size) else this

internal fun <T> List<T>.dropSuffix(suffix: Iterable<T>): List<T> =
  if (endsWith(suffix)) this.dropLast(suffix.size) else this

internal fun <T> MutableList<T>.removePrefix(prefix: T) {
  if (startsWith(prefix)) removeFirst()
}

internal fun <T> MutableList<T>.removePrefix(vararg prefix: T) {
  if (startsWith(*prefix)) delete(prefix.size)
}

internal fun <T> MutableList<T>.removePrefix(prefix: Iterable<T>) {
  if (startsWith(prefix)) delete(prefix.size)
}

internal fun <T> MutableList<T>.removeSuffix(suffix: T) {
  if (endsWith(suffix)) removeLast()
}

internal fun <T> MutableList<T>.removeSuffix(vararg suffix: T) {
  if (endsWith(suffix)) deleteLast(suffix.size)
}

internal fun <T> MutableList<T>.removeSuffix(suffix: Iterable<T>) {
  if (endsWith(suffix)) deleteLast(suffix.size)
}
/**
 * Returns `true` if at least one element matches the given [element].
 */
suspend fun <T> Flow<T>.contains(element: T): Boolean = contains { it == element }

internal fun <T> Flow<T>.retryConnection(): Flow<T> = retry(50) {
  delay(8)
  true
}
