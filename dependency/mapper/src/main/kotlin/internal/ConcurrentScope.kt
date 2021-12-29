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
 *
 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
package com.meowool.gradle.toolkit.internal

import com.meowool.sweekt.coroutines.withDefaultContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @author 凛 (RinOrz)
 */
internal class ConcurrentScope<E>(
  producerScope: ProducerScope<E>,
  private val isConcurrently: Boolean
) : ProducerScope<E> by producerScope {

  internal suspend fun <T> Iterable<T>.forEachConcurrently(
    capacity: Int = Concurrency,
    action: suspend (T) -> Unit,
  ) {
    if (this is Collection && this.isEmpty()) return
    consumeChannel(isConcurrently, Dispatchers.Default, capacity) {
      forEach {
        if (isConcurrently) send(action(it))
        else launch { send(action(it)) }
      }
    }
  }

  internal suspend fun <T> Array<T>.forEachConcurrently(
    capacity: Int = Concurrency,
    action: suspend (T) -> Unit,
  ) {
    if (this.isEmpty()) return
    consumeChannel(isConcurrently, Dispatchers.Default, capacity) {
      forEach {
        if (isConcurrently) send(action(it))
        else launch { send(action(it)) }
      }
    }
  }

  internal suspend fun <K, V> MutableMap<K, V>.forEachConcurrently(
    capacity: Int = Concurrency,
    action: suspend (key: K, value: V) -> Unit,
  ) {
    if (this.isEmpty()) return
    consumeChannel(isConcurrently, Dispatchers.Default, capacity) {
      forEach {
        if (isConcurrently) send(action(it.key, it.value))
        else launch { send(action(it.key, it.value)) }
      }
    }
  }

  internal suspend fun <K, V, R> MutableMap<K, V>.mapConcurrently(
    transform: suspend (key: K, value: V) -> R,
  ): List<R> {
    if (this.isEmpty()) return emptyList()
    return if (isConcurrently) produce(Dispatchers.Default, capacity = Concurrency) {
      forEach {
        send(async { transform(it.key, it.value) })
      }
    }.toList().awaitAll()
    else map { transform(it.key, it.value) }.toList()
  }

  internal suspend inline fun sendAll(flow: Flow<E>?) = withDefaultContext {
    flow?.collect {
      if (isConcurrently) send(it)
      else launch { send(it) }
    }
  }

  companion object {
    private const val Concurrency = 20
  }
}
