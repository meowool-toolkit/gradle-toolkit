import com.meowool.gradle.toolkit.internal.Validator
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.string.shouldContain

/**
 * @author 凛 (https://github.com/RinOrz)
 */
class ValidityTests : FreeSpec({
  "validate dependency" - {
    "group:artifact:version" {
      runCatching { Validator.validDependency(testCase.displayName) }.exceptionMessage() shouldContain "cannot contains the version"
    }
    "group id:artifact" {
      runCatching { Validator.validDependency(testCase.displayName) }.exceptionMessage() shouldContain "cannot be contains spaces"
    }
    "group-1:artifact_0.1.1" {
      shouldNotThrowAny { Validator.validDependency(testCase.displayName) }
    }
    "group.id:artifact*" {
      runCatching { Validator.validDependency(testCase.displayName) }.exceptionMessage() shouldContain "can only contains `.` or `:` or `-` or `_` or A-Z or digit"
    }
  }
  "validate path" - {
    "group.id:artifact" {
      runCatching { Validator.validPath(testCase.displayName) }.exceptionMessage() shouldContain "cannot be contains ':'"
    }
    "group.id+|artifact" {
      runCatching { Validator.validPath(testCase.displayName) }.exceptionMessage() shouldContain "can only contains `.` or `-` or `_` or A-Z or digit"
    }
  }
})