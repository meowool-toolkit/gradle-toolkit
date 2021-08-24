import com.meowool.sweekt.select
import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy
import net.bytebuddy.jar.asm.Opcodes
import java.io.File

/**
 * Write the bytecode of the dependency spec class.
 *
 * @param definingClass the defining class of ByteBuddy
 * @author 凛 (https://github.com/RinOrz)
 */
internal class ClassWriter(
  private val name: String,
  private val outputFile: File? = null,
  private var definingClass: DynamicType.Builder<Any> = createClass(name, isStatic = false)
    .modifiers(Opcodes.ACC_FINAL or Opcodes.ACC_PUBLIC)
    .name(name),
) {
  private val innerClasses = mutableListOf<ClassWriter>()

  /** Defines field of this class */
  fun field(name: String, value: String) {
    definingClass = definingClass.defineField(
      name,
      String::class.java,
      Opcodes.ACC_STATIC or Opcodes.ACC_FINAL or Opcodes.ACC_PUBLIC
    ).value(value)
  }

  /** Returns the inner class of this class */
  fun innerClass(name: String): ClassWriter {
    val innerName = this.name + "$" + name
    val inner = createClass(innerName, isStatic = true)
      .innerTypeOf(definingClass.toTypeDescription())
      .asMemberType()

    // Add inner class
    definingClass = definingClass.declaredTypes(inner.toTypeDescription())

    return ClassWriter(innerName, outputFile, definingClass = inner).also(innerClasses::add)
  }

  /** Recursively try to add children in [meta] as an inner class until it has a [DependencyMeta.notation]. */
  fun add(meta: DependencyMeta) {
    if (meta.children.isNotEmpty()) {
      val innerClass = this.innerClass(meta.name)
      meta.children.forEach { innerClass.add(it) }
    }
    // Add notation of placeholder dependency as a field
    meta.notation?.let { this.field(meta.name, "$it:_") }
  }

  /** Recursively make inner classes */
  fun make(): DynamicType.Unloaded<Any> = definingClass.make().run {
    when {
      innerClasses.isEmpty() -> this
      else -> this.include(innerClasses.map { it.make() })
    }
  }

  fun write(): File = make().toJar(outputFile)

  companion object {
    private val byteBuddy = ByteBuddy()
    private fun createClass(name: String, isStatic: Boolean) = byteBuddy
      .subclass(Any::class.java, ConstructorStrategy.Default.NO_CONSTRUCTORS)
      .modifiers(isStatic.select(Opcodes.ACC_STATIC, 0) or Opcodes.ACC_FINAL or Opcodes.ACC_PUBLIC)
      .name(name)
  }
}

internal inline fun writeJar(className: String, outputFile: File, block: (ClassWriter) -> Unit) =
  ClassWriter(className, outputFile).apply(block).write()