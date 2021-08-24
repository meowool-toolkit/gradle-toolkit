
import java.lang.System;

/**
 * The mapper to produce the jar with mapped dependencies to class field members.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\n0\u001fH\u0002J\r\u0010 \u001a\u00020!H\u0000\u00a2\u0006\u0002\b\"R\u0014\u0010\u0005\u001a\u00020\u00068BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\t\u001a\u00020\n8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\r\u001a\u00020\u000e8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\r\u0010\u000fR\u0014\u0010\u0010\u001a\u00020\n8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\fR\u001c\u0010\u0012\u001a\n \u0013*\u0004\u0018\u00010\u00060\u00068BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0014\u0010\bR\u0014\u0010\u0015\u001a\u00020\n8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0016\u0010\fR\u0016\u0010\u0017\u001a\u0004\u0018\u00010\u00068BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0018\u0010\bR\u001c\u0010\u0019\u001a\n \u0013*\u0004\u0018\u00010\u00030\u00038BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u001a\u0010\u001bR\u0016\u0010\u001c\u001a\u0004\u0018\u00010\u00068BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u001d\u0010\b\u00a8\u0006#"}, d2 = {"LDependencyMapper;", "LDependencyMapperConfiguration;", "project", "Lorg/gradle/api/Project;", "(Lorg/gradle/api/Project;)V", "cacheFile", "Ljava/io/File;", "getCacheFile", "()Ljava/io/File;", "classpath", "", "getClasspath", "()Ljava/lang/String;", "isUpdate", "", "()Z", "newCache", "getNewCache", "output", "kotlin.jvm.PlatformType", "getOutput", "outputRelative", "getOutputRelative", "rootBuild", "getRootBuild", "rootProject", "getRootProject", "()Lorg/gradle/api/Project;", "rootSettings", "getRootSettings", "fetchMvnDependencies", "Lkotlinx/coroutines/flow/Flow;", "mapping", "", "mapping$mapper", "mapper"})
public final class DependencyMapper extends DependencyMapperConfiguration {
    
    public DependencyMapper(@org.jetbrains.annotations.NotNull()
    org.gradle.api.Project project) {
        super(null);
    }
    
    private final boolean isUpdate() {
        return false;
    }
    
    private final java.lang.String getNewCache() {
        return null;
    }
    
    private final java.io.File getCacheFile() {
        return null;
    }
    
    private final java.io.File getOutput() {
        return null;
    }
    
    private final java.lang.String getOutputRelative() {
        return null;
    }
    
    private final org.gradle.api.Project getRootProject() {
        return null;
    }
    
    private final java.io.File getRootBuild() {
        return null;
    }
    
    private final java.io.File getRootSettings() {
        return null;
    }
    
    private final java.lang.String getClasspath() {
        return null;
    }
    
    public final void mapping$mapper() {
    }
    
    private final kotlinx.coroutines.flow.Flow<java.lang.String> fetchMvnDependencies() {
        return null;
    }
}