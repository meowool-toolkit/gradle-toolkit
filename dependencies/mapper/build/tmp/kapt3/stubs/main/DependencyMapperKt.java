
import java.lang.System;

@kotlin.OptIn(markerClass = {kotlinx.coroutines.ExperimentalCoroutinesApi.class, kotlinx.coroutines.FlowPreview.class})
@kotlin.Metadata(mv = {1, 5, 1}, k = 2, d1 = {"\u0000$\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a#\u0010\u0002\u001a\u00020\u0003*\u00020\u00042\u0017\u0010\u0005\u001a\u0013\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00030\u0006\u00a2\u0006\u0002\b\b\u001a#\u0010\u0002\u001a\u00020\u0003*\u00020\t2\u0017\u0010\u0005\u001a\u0013\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00030\u0006\u00a2\u0006\u0002\b\b\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"KEY", "", "dependencyMapper", "", "Lorg/gradle/api/Project;", "configuration", "Lkotlin/Function1;", "LDependencyMapperConfiguration;", "Lkotlin/ExtensionFunctionType;", "Lorg/gradle/api/initialization/Settings;", "mapper"})
public final class DependencyMapperKt {
    private static final java.lang.String KEY = "_dependencyMapper";
    
    /**
     * Configures the dependency mapper based on the given [configuration].
     */
    public static final void dependencyMapper(@org.jetbrains.annotations.NotNull()
    org.gradle.api.Project $this$dependencyMapper, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super DependencyMapperConfiguration, kotlin.Unit> configuration) {
    }
    
    /**
     * Configures the dependency mapper based on the given [configuration].
     */
    public static final void dependencyMapper(@org.jetbrains.annotations.NotNull()
    org.gradle.api.initialization.Settings $this$dependencyMapper, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super DependencyMapperConfiguration, kotlin.Unit> configuration) {
    }
}