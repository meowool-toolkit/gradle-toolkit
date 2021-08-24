
import java.lang.System;

/**
 * Write the bytecode of the dependency spec class.
 *
 * @param definingClass the defining class of ByteBuddy
 * @author 凛 (https://github.com/RinOrz)
 */
@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0000\u0018\u0000 \u00152\u00020\u0001:\u0001\u0015B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u0016\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0010\u001a\u00020\u0003J\u000e\u0010\u0011\u001a\u00020\u00002\u0006\u0010\u0002\u001a\u00020\u0003J\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u0013J\u0006\u0010\u0014\u001a\u00020\u0005R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00000\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"LClassWriter;", "", "name", "", "outputFile", "Ljava/io/File;", "definingClass", "Lnet/bytebuddy/dynamic/DynamicType$Builder;", "(Ljava/lang/String;Ljava/io/File;Lnet/bytebuddy/dynamic/DynamicType$Builder;)V", "innerClasses", "", "add", "", "meta", "LDependencyMeta;", "field", "value", "innerClass", "make", "Lnet/bytebuddy/dynamic/DynamicType$Unloaded;", "write", "Companion", "mapper"})
public final class ClassWriter {
    private final java.lang.String name = null;
    private final java.io.File outputFile = null;
    private net.bytebuddy.dynamic.DynamicType.Builder<java.lang.Object> definingClass;
    private final java.util.List<ClassWriter> innerClasses = null;
    @org.jetbrains.annotations.NotNull()
    public static final ClassWriter.Companion Companion = null;
    private static final net.bytebuddy.ByteBuddy byteBuddy = null;
    
    public ClassWriter(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.io.File outputFile, @org.jetbrains.annotations.NotNull()
    net.bytebuddy.dynamic.DynamicType.Builder<java.lang.Object> definingClass) {
        super();
    }
    
    /**
     * Defines field of this class
     */
    public final void field(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    /**
     * Returns the inner class of this class
     */
    @org.jetbrains.annotations.NotNull()
    public final ClassWriter innerClass(@org.jetbrains.annotations.NotNull()
    java.lang.String name) {
        return null;
    }
    
    /**
     * Recursively try to add children in [meta] as an inner class until it has a [DependencyMeta.notation].
     */
    public final void add(@org.jetbrains.annotations.NotNull()
    DependencyMeta meta) {
    }
    
    /**
     * Recursively make inner classes
     */
    @org.jetbrains.annotations.NotNull()
    public final net.bytebuddy.dynamic.DynamicType.Unloaded<java.lang.Object> make() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.io.File write() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J<\u0010\u0005\u001a&\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\u00010\u0001 \u0007*\u0012\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\u00010\u0001\u0018\u00010\u00060\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"LClassWriter$Companion;", "", "()V", "byteBuddy", "Lnet/bytebuddy/ByteBuddy;", "createClass", "Lnet/bytebuddy/dynamic/DynamicType$Builder;", "kotlin.jvm.PlatformType", "name", "", "isStatic", "", "mapper"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        private final net.bytebuddy.dynamic.DynamicType.Builder<java.lang.Object> createClass(java.lang.String name, boolean isStatic) {
            return null;
        }
    }
}