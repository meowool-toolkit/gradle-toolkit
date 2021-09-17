import java.io.File
import java.util.jar.JarFile

class JarClassLoader(private val jarFile: JarFile) : ClassLoader() {
  constructor(file: File) : this(JarFile(file))
  constructor(path: String) : this(JarFile(path))

  public override fun findClass(name: String): Class<*> {
    val byte = loadClassData(name)
    return defineClass(name, byte, 0, byte.size)
  }

  private fun loadClassData(className: String): ByteArray {
    val classFileName = className.replace('.', File.separatorChar) + ".class"
    return jarFile.getInputStream(jarFile.getJarEntry(classFileName)).readBytes()
  }
}