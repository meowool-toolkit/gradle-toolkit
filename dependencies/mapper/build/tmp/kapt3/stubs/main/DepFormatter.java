
import java.lang.System;

/**
 * Used to format the dependency string.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u000f\b\u0000\u0018\u0000 \u00152\u00020\u0001:\u0001\u0015BG\u0012\u0014\b\u0002\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0014\b\u0002\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0014\b\u0002\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00070\u0003\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u0004J\f\u0010\u0013\u001a\u00020\u0004*\u00020\u0004H\u0002J\f\u0010\u0014\u001a\u00020\u0004*\u00020\u0004H\u0002R&\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00070\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR&\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\n\"\u0004\b\u000e\u0010\fR&\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\n\"\u0004\b\u0010\u0010\f\u00a8\u0006\u0016"}, d2 = {"LDepFormatter;", "", "replaceNotation", "Lkotlin/Function1;", "", "replaceName", "capitalizeFirstLetter", "", "(Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V", "getCapitalizeFirstLetter", "()Lkotlin/jvm/functions/Function1;", "setCapitalizeFirstLetter", "(Lkotlin/jvm/functions/Function1;)V", "getReplaceName", "setReplaceName", "getReplaceNotation", "setReplaceNotation", "format", "rawNotation", "joinPath", "mayUpperCase", "Companion", "mapper"})
public final class DepFormatter {
    @org.jetbrains.annotations.NotNull()
    private kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.String> replaceNotation;
    @org.jetbrains.annotations.NotNull()
    private kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.String> replaceName;
    @org.jetbrains.annotations.NotNull()
    private kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.Boolean> capitalizeFirstLetter;
    @org.jetbrains.annotations.NotNull()
    public static final DepFormatter.Companion Companion = null;
    @org.jetbrains.annotations.NotNull()
    private static final DepFormatter Default = null;
    
    public DepFormatter() {
        super();
    }
    
    public DepFormatter(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.String> replaceNotation, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.String> replaceName, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.Boolean> capitalizeFirstLetter) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlin.jvm.functions.Function1<java.lang.String, java.lang.String> getReplaceNotation() {
        return null;
    }
    
    public final void setReplaceNotation(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.String> p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlin.jvm.functions.Function1<java.lang.String, java.lang.String> getReplaceName() {
        return null;
    }
    
    public final void setReplaceName(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.String> p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlin.jvm.functions.Function1<java.lang.String, java.lang.Boolean> getCapitalizeFirstLetter() {
        return null;
    }
    
    public final void setCapitalizeFirstLetter(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.Boolean> p0) {
    }
    
    /**
     * Return the notation of dependency after formatting [rawNotation].
     *
     * ```
     * // Inputs:
     * DepFormatter({ it.replace("foo.bar", "foo") })
     *  .format("foo.bar.gav:gav-test")
     *
     * DepFormatter().format("one.dep.user:core-ext")
     *
     * // Outputs:
     * Foo.Bar.Gav.Test
     * One.Dep.User.Core.Ext
     * ```
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String format(@org.jetbrains.annotations.NotNull()
    java.lang.String rawNotation) {
        return null;
    }
    
    private final java.lang.String mayUpperCase(java.lang.String $this$mayUpperCase) {
        return null;
    }
    
    /**
     * Each part of the path is a name, and replaced it via [replaceName]
     */
    private final java.lang.String joinPath(java.lang.String $this$joinPath) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2 = {"LDepFormatter$Companion;", "", "()V", "Default", "LDepFormatter;", "getDefault", "()LDepFormatter;", "mapper"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final DepFormatter getDefault() {
            return null;
        }
    }
}