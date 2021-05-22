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
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CHAR_SEQUENCE
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

/**
 * @author 凛 (https://github.com/RinOrz)
 */
internal class DependencyGenerator(
  private val fileName: String,
  private val dependencies: List<Dependency>,
  private var output: Any,
  private val autoClose: Boolean
) {
  /**
   * 缓存所有加入过的 type
   */
  private var classes = mutableMapOf<String, TypeBuilder>()

  /**
   * 记录已经生成的依赖的组 id 根名称
   *
   * @see Dependency.parent 例如：a.b.c 为 a
   */
  private val buildFinish = mutableListOf<String>()

  fun generate() {
    dependencies.forEach {
      it.prepareAncestors()
      it.join()
    }

    val output = (output as? File)?.bufferedWriter() ?: output as Appendable
    if (autoClose && output is AutoCloseable) {
      output.use(::writeTo)
    } else {
      writeTo(output)
    }
  }

  private fun Dependency.prepareAncestors() {
    pathSplit.forEachIndexed { index, name ->
      val path = takePath(index + 1)
      val builder = classes.getOrPut(path) { TypeBuilder(name) }
      // 非根类时需要将类添加到父类中
      if (index != 0) {
        val parent = takePath(index)
        classes[parent]!!.add(builder)
      }
    }
  }

  private fun Dependency.join() {
    require(name.isNotBlank()) { "Illegal name, the name cannot be blank. parent: $parent, full: $full" }

    // object Foo : CharSequence by "groupId:artifactName:_"
    val dependencyNotation = TypeSpec.objectBuilder(name)
      .superclass(ClassName("", "_D"))
      .addSuperclassConstructorParameter("%S", "$full:_")

    // 如果 class 存在则将其改为 object
    val path = classes[path]
    if (path != null) {
      path.builder = dependencyNotation.apply {
        addTypes(path.builder.typeSpecs)
      }
    } else {
      // 否则我们往父类添加
      classes[parent]!!.builder.addType(dependencyNotation.build())
    }
  }

  private fun buildLibraries() = TypeSpec
    .classBuilder(fileName)
    .addKdoc(
      """
      Some automatically generated dependency references.

      Note that this class is generated by [dependencies-generator](https://github.com/meowool-toolkit/gradle-dsl-x/dependencies/generator), don't edit it.

      @author 凛 (https://github.com/RinOrz)
      """.trimIndent()
    )
    .apply {
      classes.forEach { (path, builder) ->
        // 得到祖先名称，既 a.b.c 的 a
        val ancestor = path.substringBefore(".")
        if (!buildFinish.contains(ancestor)) {
          buildFinish += ancestor
          addType(builder.build())
        }
      }
    }.build()

  private fun writeTo(appendable: Appendable) {
    FileSpec.builder("", fileName)
      .addAnnotation(
        AnnotationSpec.builder(Suppress::class)
          .addMember("\"RedundantVisibilityModifier\", \"ClassName\", \"unused\"")
          .build()
      )
      .addType(buildLibraries())
      .addType(
        TypeSpec.classBuilder("_D")
          .addModifiers(KModifier.ABSTRACT)
          .primaryConstructor(
            FunSpec.constructorBuilder()
              .addParameter("dependencyNotation", String::class)
              .build()
          )
          .addSuperinterface(CHAR_SEQUENCE, "dependencyNotation")
          .addProperty(
            PropertySpec.builder("dependencyNotation", String::class, KModifier.PRIVATE)
              .initializer("dependencyNotation")
              .build()
          )
          .addFunction(
            FunSpec.builder("toString")
              .addModifiers(KModifier.OVERRIDE)
              .returns(STRING)
              .addStatement("return dependencyNotation")
              .build()
          )
          .build()
      )
      .build()
      .writeTo(appendable)
  }
}