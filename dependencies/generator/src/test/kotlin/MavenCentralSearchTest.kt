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
import org.junit.Test

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class MavenCentralSearchTest {
  @Test fun search() {
    val group = "com.google.accompanist"
    assertContains(
      DependenciesSearcher.search(group),
      """
        com.google.accompanist:accompanist-systemuicontroller
        com.google.accompanist:accompanist-pager-indicators
        com.google.accompanist:accompanist-pager
        com.google.accompanist:accompanist-insets
        com.google.accompanist:accompanist-imageloading-core
        com.google.accompanist:accompanist-glide
        com.google.accompanist:accompanist-flowlayout
        com.google.accompanist:accompanist-coil
        com.google.accompanist:accompanist-appcompat-theme
        com.google.accompanist:accompanist-picasso
      """.trimIndent(),
    )
  }
}
