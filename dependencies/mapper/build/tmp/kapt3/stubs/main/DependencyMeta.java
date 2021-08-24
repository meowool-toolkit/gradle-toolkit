
import java.lang.System;

/**
 * @param notation the original notation of dependency
 * @see flattenToDepTree
 * @author 凛 (https://github.com/RinOrz)
 */
@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010#\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0080\b\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00000\u0006\u00a2\u0006\u0002\u0010\u0007J\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0010\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00000\u0006H\u00c6\u0003J/\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00000\u0006H\u00c6\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0016\u001a\u00020\u0017H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0003H\u00d6\u0001R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00000\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\u000b\"\u0004\b\r\u0010\u000e\u00a8\u0006\u0019"}, d2 = {"LDependencyMeta;", "", "name", "", "notation", "children", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/Set;)V", "getChildren", "()Ljava/util/Set;", "getName", "()Ljava/lang/String;", "getNotation", "setNotation", "(Ljava/lang/String;)V", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "mapper"})
public final class DependencyMeta {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String notation;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<DependencyMeta> children = null;
    
    /**
     * @param notation the original notation of dependency
     * @see flattenToDepTree
     * @author 凛 (https://github.com/RinOrz)
     */
    @org.jetbrains.annotations.NotNull()
    public final DependencyMeta copy(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String notation, @org.jetbrains.annotations.NotNull()
    java.util.Set<DependencyMeta> children) {
        return null;
    }
    
    /**
     * @param notation the original notation of dependency
     * @see flattenToDepTree
     * @author 凛 (https://github.com/RinOrz)
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object p0) {
        return false;
    }
    
    /**
     * @param notation the original notation of dependency
     * @see flattenToDepTree
     * @author 凛 (https://github.com/RinOrz)
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * @param notation the original notation of dependency
     * @see flattenToDepTree
     * @author 凛 (https://github.com/RinOrz)
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    public DependencyMeta(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String notation, @org.jetbrains.annotations.NotNull()
    java.util.Set<DependencyMeta> children) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getNotation() {
        return null;
    }
    
    public final void setNotation(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<DependencyMeta> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<DependencyMeta> getChildren() {
        return null;
    }
}