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
@file:Suppress("SuspendFunctionOnCoroutineScope")

package com.meowool.gradle.toolkit.internal

import com.meowool.sweekt.className
import com.meowool.sweekt.datetime.nanos
import com.meowool.sweekt.select
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
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
import java.time.Duration
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

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
      Duration::class,
      object : KSerializer<Duration> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(Duration::class.className, PrimitiveKind.LONG)
        @Suppress("DEPRECATION") // FIXME Gradle Kotlin DSL Support duration
        override fun serialize(encoder: Encoder, value: Duration) = encoder.encodeLong(value.toNanos())
        override fun deserialize(decoder: Decoder): Duration = Duration.ofNanos(decoder.decodeLong())
      }
    )
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

@Suppress("SuspendFunctionOnCoroutineScope")
internal suspend fun CoroutineScope.consumeChannel(
  isConcurrently: Boolean = true,
  context: CoroutineContext = EmptyCoroutineContext,
  capacity: Int = 0,
  block: suspend ConcurrentScope<Any>.() -> Unit
) = produce(context, capacity) { block(ConcurrentScope(this, isConcurrently)) }.consumeEach { }

internal fun <E> concurrentFlow(
  isConcurrently: Boolean = true,
  @BuilderInference block: suspend ConcurrentScope<E>.() -> Unit
): Flow<E> = channelFlow { block(ConcurrentScope(this, isConcurrently)) }

internal fun <T, R> Flow<T>.flatMapConcurrently(
  concurrency: Int = Int.MAX_VALUE,
  transform: suspend (T) -> Flow<R>
): Flow<R> = flatMapMerge(concurrency, transform)

internal suspend fun <T> CoroutineScope.withTimeout(timeout: Duration?, block: suspend CoroutineScope.() -> T): T {
  if (timeout == null) return block()
  return kotlinx.coroutines.withTimeout(timeout.toMillis(), block)
}
fun <T> Flow<T>.distinct(): Flow<T> = distinctBy { it }

fun <T, K> Flow<T>.distinctBy(selector: (T) -> K): Flow<T> = flow {
  val keySet = mutableSetOf<K>()
  collect { value ->
    if (keySet.add(selector(value)))
      emit(value)
  }
}

internal fun <T> Flow<T>.retryConnection(): Flow<T> = retry(50) {
  delay(8)
  true
}
