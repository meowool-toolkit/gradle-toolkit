
import java.lang.System;

/**
 * A client for [MvnRepository](https://mvnrepository.com/) website.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\u0018\u0000 \u001a2\u00020\u0001:\u0001\u001aB\u000f\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J#\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\rJ\u001b\u0010\u000e\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\nH\u0080@\u00f8\u0001\u0000\u00a2\u0006\u0004\b\u000f\u0010\u0010J\b\u0010\u0011\u001a\u00020\u0012H\u0016J\u001e\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\n0\u00142\u0006\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u0015\u001a\u00020\u0016J&\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\n0\u00142\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0010\u0010\u0018\u001a\n \u0019*\u0004\u0018\u00010\n0\nH\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u001b"}, d2 = {"LMvnRepositoryClient;", "Ljava/lang/AutoCloseable;", "logLevel", "Lio/ktor/client/features/logging/LogLevel;", "(Lio/ktor/client/features/logging/LogLevel;)V", "client", "Lio/ktor/client/HttpClient;", "artifactsHtml", "Lorg/jsoup/nodes/Document;", "group", "", "page", "", "(Ljava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "calculatePageCount", "calculatePageCount$mapper", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "close", "", "fetchArtifacts", "Lkotlinx/coroutines/flow/Flow;", "recursively", "", "fetchArtifactsByPage", "randomIP", "kotlin.jvm.PlatformType", "Companion", "mapper"})
public final class MvnRepositoryClient implements java.lang.AutoCloseable {
    private final io.ktor.client.HttpClient client = null;
    @org.jetbrains.annotations.NotNull()
    public static final MvnRepositoryClient.Companion Companion = null;
    private static final java.lang.String baseUrl = "https://mvnrepository.com";
    
    public MvnRepositoryClient() {
        super();
    }
    
    public MvnRepositoryClient(@org.jetbrains.annotations.NotNull()
    io.ktor.client.features.logging.LogLevel logLevel) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.String> fetchArtifacts(@org.jetbrains.annotations.NotNull()
    java.lang.String group, boolean recursively) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object calculatePageCount$mapper(@org.jetbrains.annotations.NotNull()
    java.lang.String group, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> p1) {
        return null;
    }
    
    private final kotlinx.coroutines.flow.Flow<java.lang.String> fetchArtifactsByPage(java.lang.String group, boolean recursively, int page) {
        return null;
    }
    
    private final java.lang.Object artifactsHtml(java.lang.String group, int page, kotlin.coroutines.Continuation<? super org.jsoup.nodes.Document> p2) {
        return null;
    }
    
    private final java.lang.String randomIP() {
        return null;
    }
    
    @java.lang.Override()
    public void close() {
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"LMvnRepositoryClient$Companion;", "", "()V", "baseUrl", "", "mapper"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}