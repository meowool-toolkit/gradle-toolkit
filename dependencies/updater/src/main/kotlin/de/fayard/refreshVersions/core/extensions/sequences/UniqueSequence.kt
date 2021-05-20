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
 */
package de.fayard.refreshVersions.core.extensions.sequences

internal fun <T, K> Sequence<T>.uniqueBy(
  onDuplicate: (key: K) -> Nothing = { key ->
    throw IllegalArgumentException(
      "Sequence contains more than one element with the key $key."
    )
  },
  selector: (T) -> K
): Sequence<T> {
  return UniqueSequence(this, onDuplicate, selector)
}

private class UniqueSequence<T, K>(
  private val source: Sequence<T>,
  private val onDuplicateFound: (key: K) -> Nothing,
  private val keySelector: (T) -> K
) : Sequence<T> {

  override fun iterator(): Iterator<T> = object : AbstractIterator<T>() {
    private val observed = HashSet<K>()
    private val sourceIterator = source.iterator()

    override fun computeNext() {
      while (sourceIterator.hasNext()) {
        val next = sourceIterator.next()
        val key = keySelector(next)

        if (observed.add(key)) {
          setNext(next)
          return
        } else {
          onDuplicateFound(key)
        }
      }

      done()
    }
  }
}
