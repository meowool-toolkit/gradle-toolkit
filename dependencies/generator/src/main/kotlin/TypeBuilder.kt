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
import com.squareup.kotlinpoet.TypeSpec

/**
 * @author 凛 (https://github.com/RinOrz)
 */
internal class TypeBuilder(name: String) {
  var builder = TypeSpec.classBuilder(name)
  private val children = mutableSetOf<TypeBuilder>()

  fun add(children: TypeBuilder) = apply {
    this.children.add(children)
  }

  /**
   * 循环创建每个子类
   */
  fun build(): TypeSpec = builder
    .addTypes(children.map { it.build() })
    .build()
}
