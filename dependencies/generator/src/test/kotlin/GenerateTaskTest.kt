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
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import java.io.StringWriter
import kotlin.test.assertEquals

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class GenerateTaskTest {
  @Test fun generate() {
    val output = StringWriter()
    ProjectBuilder.builder().build().generateDependencies {
      classNameTransform {
        when (it) {
          "io" -> "IO"
          else -> it.replace("androidx", "AndroidX")
            .replace("vectordrawable", "vectorDrawable")
            .replace("systemuicontroller", "SystemUiController")
        }
      }
      classParentTransform {
        it.removePrefix("com.")
      }
      replaceGroups(
        """
          androidx.appcompat -> appcompat
        """.trimIndent()
      )
      declareDependencies(
        """
          androidx.compose.ui:ui
          androidx.activity:activity-compose [parent=androidx.compose, name=Activity]
          androidx.activity:activity-ktx [parent=Extension]
          androidx.appcompat:appcompat
          androidx.vectordrawable:vectordrawable
          androidx.vectordrawable:vectordrawable-animated [name=Animation]
          com.google.guava:guava-io
          com.google.accompanist:accompanist-systemuicontroller
        """.trimIndent()
      )
      outputTo(output)
    }

    assertEquals(
      """
        @file:Suppress("RedundantVisibilityModifier", "ClassName", "unused")
        import kotlin.CharSequence
        import kotlin.String
        import kotlin.Suppress
        /**
         * Some automatically generated dependency references.
         *
         * Note that this class is generated by
         * [dependencies-generator](https://github.com/meowool-toolkit/gradle-dsl-x/dependencies/generator),
         * don't edit it.
         *
         * @author 凛 (https://github.com/RinOrz)
         */
        public class Libs {
          public class AndroidX {
            public class Compose {
              public object Ui : _D("androidx.compose.ui:ui:_")
              public object Activity : _D("androidx.activity:activity-compose:_")
            }
            public object VectorDrawable : _D("androidx.vectordrawable:vectordrawable:_") {
              public object Animation : _D("androidx.vectordrawable:vectordrawable-animated:_")
            }
          }
          public class Extension {
            public class Activity {
              public object Ktx : _D("androidx.activity:activity-ktx:_")
            }
          }
          public object Appcompat : _D("androidx.appcompat:appcompat:_")
          public class Google {
            public class Guava {
              public object IO : _D("com.google.google.guava:guava-io:_")
            }
            public class Accompanist {
              public object SystemUiController :
                  _D("com.google.accompanist:accompanist-systemuicontroller:_")
            }
          }
        }
        public abstract class _D(
          private val dependencyNotation: String
        ) : CharSequence by dependencyNotation {
          public override fun toString(): String = dependencyNotation
        }
      """.trimIndent(),
      output.toString().filterNotBlankLines(),
    )
  }

  private fun String.filterNotBlankLines() = this.lineSequence()
    .filter { it.isNotBlank() }
    .joinToString("\n")
}
