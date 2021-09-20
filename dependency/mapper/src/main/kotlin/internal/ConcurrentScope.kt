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
package internal

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.meowool.sweekt.coroutines.withDefault as withDefaultContext

/**
 * @author 凛 (https://github.com/RinOrz)
 */
internal class ConcurrentScope<E>(producerScope: ProducerScope<E>) : ProducerScope<E> by producerScope {

  internal suspend inline fun <T> Iterable<T>.forEachConcurrently(
    crossinline action: suspend (T) -> Unit,
  ) {
    if (this is Collection && this.isEmpty()) return
    withDefaultContext {
      forEach { launch { action(it) } }
    }
  }

  internal suspend inline fun <T> Array<T>.forEachConcurrently(
    crossinline action: suspend (T) -> Unit,
  ) {
    if (this.isEmpty()) return
    withDefaultContext {
      forEach { launch { action(it) } }
    }
  }

  internal suspend inline fun <K, V> MutableMap<K, V>.forEachConcurrently(
    crossinline action: suspend (Map.Entry<K, V>) -> Unit,
  ) {
    if (this.isEmpty()) return
    withDefaultContext {
      forEach { launch { action(it) } }
    }
  }

  internal suspend inline fun <K, V, R> MutableMap<K, V>.mapConcurrently(
    crossinline transform: suspend (Map.Entry<K, V>) -> R,
  ): List<R> {
    if (this.isEmpty()) return emptyList()
    return withDefaultContext {
      map {
        async { transform(it) }
      }.awaitAll()
    }
  }

  internal suspend inline fun sendAll(flow: Flow<E>?) = withDefaultContext {
    flow?.collect { launch { send(it) } }
  }

  internal suspend inline fun sendList(list: List<E>?) = withDefaultContext {
    list?.forEach { launch { send(it) } }
  }
}
