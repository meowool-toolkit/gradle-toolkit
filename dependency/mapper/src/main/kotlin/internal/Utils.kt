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
@file:Suppress("EXPERIMENTAL_API_USAGE", "SuspendFunctionOnCoroutineScope")
@file:OptIn(ExperimentalTypeInference::class)

package com.meowool.gradle.toolkit.internal

import com.meowool.sweekt.className
import com.meowool.sweekt.coroutines.contains
import com.meowool.sweekt.isEnglish
import com.meowool.sweekt.isEnglishNotPunctuation
import com.meowool.sweekt.iteration.endsWith
import com.meowool.sweekt.iteration.size
import com.meowool.sweekt.iteration.startsWith
import com.meowool.sweekt.select
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
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
    polymorphic(MapDeclaration::class) {
      subclass(LibraryDependencyDeclarationImpl::class)
      subclass(ProjectDependencyDeclarationImpl::class)
      subclass(PluginDependencyDeclarationImpl::class)
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

internal suspend inline fun <T> ProducerScope<T>.sendAll(crossinline block: suspend () -> Flow<T>?) =
  block()?.collect { launch { send(it) } }

internal suspend inline fun <T> ProducerScope<T>.sendList(crossinline block: suspend () -> List<T>?) =
  block()?.forEach { launch { send(it) } }

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
