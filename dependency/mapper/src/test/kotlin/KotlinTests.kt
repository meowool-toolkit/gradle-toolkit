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
import com.meowool.gradle.toolkit.internal.concurrentFlow
import com.meowool.sweekt.coroutines.flowOnIO
import com.meowool.sweekt.throwIf
import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.withTimeout

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class KotlinTests : StringSpec({
  "duration" {
    var lastRetry: Long? = null
    val retryTimeoutMillis = 1000L

    concurrentFlow {
      suspend fun impl() {
        var count = 0
        delay(100)
        send(10)
        throwIf(count < 100) {
          // Start waiting for retry timeout
          if (lastRetry == null) lastRetry = System.currentTimeMillis()
          throw ResultsMissingException(count)
        }
      }
      when {
        lastRetry != null -> {
          // Remaining timeout
          val consumed = System.currentTimeMillis() - lastRetry!!
          println("consumed = $consumed")
          withTimeout(retryTimeoutMillis - consumed) { impl() }
        }
        else -> impl()
      }
    }.flowOnIO().retry(Long.MAX_VALUE) { it is ResultsMissingException }.collect()
  }
})

private class ResultsMissingException(count: Int) :
  IllegalStateException()
