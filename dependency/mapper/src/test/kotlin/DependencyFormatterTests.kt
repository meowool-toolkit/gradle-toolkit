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
import com.meowool.gradle.toolkit.DependencyFormatter
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.scopes.FreeSpecTerminalContext
import io.kotest.matchers.shouldBe

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class DependencyFormatterTests : FreeSpec({

  operator fun FreeSpecTerminalContext.invoke(formatter: DependencyFormatter) {
    val raw = testCase.displayName.substringBefore(" -> ")
    val expected = testCase.displayName.substringAfter(" -> ")
    formatter.toPath(raw) shouldBe expected
  }

  "Default" - {
    val formatter = DependencyFormatter().apply { mergeDuplicateLevels() }
    "foo.bar:foo.bar -> Foo.Bar" { this(formatter) }
    "foo.bar.gav:gav -> Foo.Bar.Gav" { this(formatter) }
    "foo.bar.gav:gav-test -> Foo.Bar.Gav.Test" { this(formatter) }
    "foo.bar.gav:bar-gav-core -> Foo.Bar.Gav.Core" { this(formatter) }
    "foo.bar.gav:xyz-10.2g.2 -> Foo.Bar.Gav.Xyz_10_2g_2" { this(formatter) }
    "foo.bar.gav:2020 -> Foo.Bar.Gav_2020" { this(formatter) }
  }

  "Replaced" - {
    val formatter = DependencyFormatter().apply {
      mergeDuplicateLevels()
      onStart { it.replace("noodle.egg", "com.food").replace("org.jetbrains.kotlin", "kotlin") }
      onEachName { if (it == "eat") "custom" else it }
      isCapitalize { false }
    }
    "org.jetbrains.kotlin:kotlin-stdlib-jdk8 -> kotlin.stdlib.jdk8" { this(formatter) }
    "noodle.egg:noodle.egg -> com.food" { this(formatter) }
    "foo.bar.eat:eat -> foo.bar.custom" { this(formatter) }
  }

  "MergeDuplicateLevels" - {
    val formatter = DependencyFormatter()
    "foo.bar.eat:foo.bar.eat -> Foo.Bar.Eat" { this(formatter) }
    "foo.bar:foo.bar.baz -> Foo.Bar.Baz" { this(formatter) }
  }
})
