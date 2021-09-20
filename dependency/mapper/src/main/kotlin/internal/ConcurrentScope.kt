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