/*
 * Copyright (c) $\YEAR. The Meowool Organization Open Source Project
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
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * @author 凛 (https://github.com/RinOrz)
 */
internal object DependenciesSearcher {
  private val client = OkHttpClient()
  private val moshi = Moshi.Builder().build()
  private val mavenCentralAdapter = moshi.adapter(MavenCentralData::class.java)

  fun search(content: String): String = content.lineSequence().joinToString("\n") {
    val row = it.removeBlanks()
    val keyword = row.substringBeforeLast('[')
    val searchAll = when {
      row.contains('[') -> {
        require(row.contains(']')) { "Illegal search request,  missing ']' for $row" }
        row.substringAfterLast('[').removeSuffix("]") == "all"
      }
      // Search only group by default
      else -> false
    }
    search(keyword, searchAll)
  }

  private fun search(
    keyword: String,
    searchAll: Boolean = false,
    number: Int? = null,
  ): String {
    val query = if (searchAll) keyword else "g:$keyword"
    val request = Request.Builder()
      .url("https://search.maven.org/solrsearch/select?q=$query&rows=${number ?: 1}&wt=json")
      .build()

    client.newCall(request).execute().use { response ->
      if (!response.isSuccessful) return ""

      val data = mavenCentralAdapter.fromJson(response.body!!.source())!!.response

      return if (number == null) {
        // 用得到的数量再搜索一次
        search(keyword, searchAll, data.numFound)
      } else {
        data.docs.joinToString("\n") { it.group + ":" + it.artifact }
      }
    }
  }
}
