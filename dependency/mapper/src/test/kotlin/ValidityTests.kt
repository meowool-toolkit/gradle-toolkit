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

 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
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
