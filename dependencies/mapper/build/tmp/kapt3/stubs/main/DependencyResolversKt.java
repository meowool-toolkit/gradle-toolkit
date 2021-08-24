
import java.lang.System;

@kotlin.Suppress(names = {"SpellCheckingInspection"})
@kotlin.Metadata(mv = {1, 5, 1}, k = 2, d1 = {"\u0000(\n\u0000\n\u0002\u0010\u0002\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u001e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a4\u0010\u0000\u001a\u00020\u0001*\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00040\u00022\u0006\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003H\u0002\u001a+\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\t*\b\u0012\u0004\u0012\u00020\u00030\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0080@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\r\u001a\f\u0010\u000e\u001a\u00020\u0003*\u00020\u0003H\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u000f"}, d2 = {"addDependency", "", "", "", "LDependencyMeta;", "raw", "path", "parent", "flattenToDepTree", "", "Lkotlinx/coroutines/flow/Flow;", "formatter", "LDepFormatter;", "(Lkotlinx/coroutines/flow/Flow;LDepFormatter;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "validDepNotation", "mapper"})
public final class DependencyResolversKt {
    
    /**
     * Convert all defined dependencies to metas and flattens.
     *
     * ```
     * // inputs
     * com.compose.ui:ui
     * com.compose.material:material
     * com.ads:identifier
     * google.webkit:core
     *
     * // outputs
     * com {
     *  compose {
     *    ui
     *    material
     *  }
     *  ads
     * }
     * google {
     *  webkit
     * }
     * ```
     *
     * @author 凛 (https://github.com/RinOrz)
     */
    @org.jetbrains.annotations.Nullable()
    public static final java.lang.Object flattenToDepTree(@org.jetbrains.annotations.NotNull()
    kotlinx.coroutines.flow.Flow<java.lang.String> $this$flattenToDepTree, @org.jetbrains.annotations.NotNull()
    DepFormatter formatter, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.Collection<DependencyMeta>> p2) {
        return null;
    }
    
    /**
     * Convert the given [path] of dependency group and artifact to dependency meta and add it's to this map.
     *
     * @param raw the raw notation of the dependency, see [DependencyMeta.notation]
     * @param path if the value has `.`, the [DependencyMeta.notation] will be `null`
     * @param parent the parent path of specified [path], if exists
     */
    private static final void addDependency(java.util.Map<java.lang.String, DependencyMeta> $this$addDependency, java.lang.String raw, java.lang.String path, java.lang.String parent) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String validDepNotation(@org.jetbrains.annotations.NotNull()
    java.lang.String $this$validDepNotation) {
        return null;
    }
}