import com.meowool.gradle.toolkit.internal.DepFormatter
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.scopes.FreeSpecTerminalContext
import io.kotest.matchers.shouldBe

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class DepsFormatTestes : FreeSpec({

  operator fun FreeSpecTerminalContext.invoke(formatter: DepFormatter) {
    val raw = testCase.displayName.substringBefore(" -> ")
    val expected = testCase.displayName.substringAfter(" -> ")
    formatter.format(raw) shouldBe expected
  }

  "Default" - {
    val formatter = DepFormatter.Default
    "foo.bar:foo.bar -> Foo.Bar" { this(formatter) }
    "foo.bar.gav:gav -> Foo.Bar.Gav" { this(formatter) }
    "foo.bar.gav:gav-test -> Foo.Bar.Gav.Test" { this(formatter) }
    "foo.bar.gav:bar-gav-core -> Foo.Bar.Gav.Core" { this(formatter) }
    "foo.bar.gav:xyz-10.2g.2 -> Foo.Bar.Gav.Xyz_10_2g_2" { this(formatter) }
    "foo.bar.gav:2020 -> Foo.Bar.Gav._2020" { this(formatter) }
  }

  "Replaced" - {
    val formatter = DepFormatter(
      replaceNotation = { it.replace("noodle.egg", "com.food") },
      nameReplacers = { it.replace("eat", "custom") },
      capitalizeFirstLetter = { false }
    )
    "noodle.egg:noodle.egg -> com.food" { this(formatter) }
    "foo.bar.eat:eat -> foo.bar.custom" { this(formatter) }
  }
})