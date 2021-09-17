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
      onStart { it.replace("noodle.egg", "com.food") }
      onEachName { if (it == "eat") "custom" else it }
      isCapitalize { false }
    }
    "noodle.egg:noodle.egg -> com.food" { this(formatter) }
    "foo.bar.eat:eat -> foo.bar.custom" { this(formatter) }
  }
})