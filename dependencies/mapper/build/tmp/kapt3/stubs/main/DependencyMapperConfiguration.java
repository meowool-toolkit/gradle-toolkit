
import java.lang.System;

/**
 * The optional configuration of Dependencies Mapper.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u0002\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0016\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001f\u0010-\u001a\u00020.2\u0012\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00070/\"\u00020\u0007\u00a2\u0006\u0002\u00100JG\u00101\u001a\u00020.2:\u0010\u0013\u001a\u001e\u0012\u001a\b\u0001\u0012\u0016\u0012\b\u0012\u00060\u0007j\u0002`\u0015\u0012\b\u0012\u00060\u0007j\u0002`\u0016020/\"\u0016\u0012\b\u0012\u00060\u0007j\u0002`\u0015\u0012\b\u0012\u00060\u0007j\u0002`\u001602\u00a2\u0006\u0002\u00103J\u001f\u00104\u001a\u00020.2\u0012\u00105\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00070/\"\u00020\u0007\u00a2\u0006\u0002\u00100J\u0006\u00106\u001a\u00020.J+\u00107\u001a\u00020.2#\b\u0002\u00108\u001a\u001d\u0012\u0013\u0012\u00110\u0007\u00a2\u0006\f\b9\u0012\b\b:\u0012\u0004\b\b(:\u0012\u0004\u0012\u00020\u001d0\u001cJ\u000e\u0010;\u001a\u00020.2\u0006\u0010<\u001a\u00020#J\u000e\u0010;\u001a\u00020.2\u0006\u0010<\u001a\u00020=J\u001a\u0010>\u001a\u00020.2\u0012\u0010?\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00070\u001cJ\u001a\u0010@\u001a\u00020.2\u0012\u0010?\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00070\u001cJ\u001a\u0010A\u001a\u00020.2\u0012\u00108\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u001d0\u001cR\u001a\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0014\u0010\n\u001a\u00020\u000bX\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001a\u0010\u000e\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R(\u0010\u0013\u001a\u0016\u0012\b\u0012\u00060\u0007j\u0002`\u0015\u0012\b\u0012\u00060\u0007j\u0002`\u00160\u0014X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u001a\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\tR(\u0010\u001b\u001a\u0010\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u001d\u0018\u00010\u001cX\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001f\"\u0004\b \u0010!R\u001c\u0010\"\u001a\u0004\u0018\u00010#X\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b$\u0010%\"\u0004\b&\u0010\'R\u0014\u0010\u0002\u001a\u00020\u0003X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010)R\u001a\u0010*\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b+\u0010\u0010\"\u0004\b,\u0010\u0012\u00a8\u0006B"}, d2 = {"LDependencyMapperConfiguration;", "", "project", "Lorg/gradle/api/Project;", "(Lorg/gradle/api/Project;)V", "dependencies", "", "", "getDependencies$mapper", "()Ljava/util/List;", "formatter", "LDepFormatter;", "getFormatter$mapper", "()LDepFormatter;", "jarName", "getJarName", "()Ljava/lang/String;", "setJarName", "(Ljava/lang/String;)V", "mappingDependencies", "", "LDependencyNotation;", "LDependencyMapped;", "getMappingDependencies$mapper", "()Ljava/util/Map;", "mvnGroups", "getMvnGroups$mapper", "needUpdate", "Lkotlin/Function1;", "", "getNeedUpdate$mapper", "()Lkotlin/jvm/functions/Function1;", "setNeedUpdate$mapper", "(Lkotlin/jvm/functions/Function1;)V", "outputFile", "Ljava/io/File;", "getOutputFile$mapper", "()Ljava/io/File;", "setOutputFile$mapper", "(Ljava/io/File;)V", "getProject$mapper", "()Lorg/gradle/api/Project;", "rootClassName", "getRootClassName", "setRootClassName", "addDependencies", "", "", "([Ljava/lang/String;)V", "addDependenciesMapping", "Lkotlin/Pair;", "([Lkotlin/Pair;)V", "addMvnDependencies", "groups", "alwaysUpdate", "capitalizeFirstLetter", "predicate", "Lkotlin/ParameterName;", "name", "outputTo", "directory", "Ljava/nio/file/Path;", "transformName", "transformation", "transformNotation", "updateWhen", "mapper"})
public class DependencyMapperConfiguration {
    @org.jetbrains.annotations.NotNull()
    private final org.gradle.api.Project project = null;
    
    /**
     * The name of the generated source file (without extension).
     *
     * At the same time it will affect the root class name.
     */
    @org.jetbrains.annotations.NotNull()
    private java.lang.String rootClassName = "Libs";
    
    /**
     * The name of generate the jar with mapped dependencies.
     */
    @org.jetbrains.annotations.NotNull()
    private java.lang.String jarName = "deps-mapping.jar";
    @org.jetbrains.annotations.NotNull()
    private final DepFormatter formatter = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> dependencies = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> mvnGroups = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.String> mappingDependencies = null;
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function1<? super org.gradle.api.Project, java.lang.Boolean> needUpdate;
    @org.jetbrains.annotations.Nullable()
    private java.io.File outputFile;
    
    public DependencyMapperConfiguration(@org.jetbrains.annotations.NotNull()
    org.gradle.api.Project project) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final org.gradle.api.Project getProject$mapper() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRootClassName() {
        return null;
    }
    
    public final void setRootClassName(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getJarName() {
        return null;
    }
    
    public final void setJarName(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    /**
     * Capitalizes the first letter of the generated classes or fields name when [predicate] is true.
     *
     * For example `androidx.compose` will replace to `Androidx.Compose`
     */
    public final void capitalizeFirstLetter(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.Boolean> predicate) {
    }
    
    /**
     * Adds the given [dependencies] that need to be generated.
     *
     * ```
     * addDependencies(
     *  "androidx.compose.ui:ui",
     *  "androidx.appcompat:appcompat",
     *  "androidx.activity:activity-compose",
     * )
     * ```
     */
    public final void addDependencies(@org.jetbrains.annotations.NotNull()
    java.lang.String... dependencies) {
    }
    
    /**
     * Add the given dependencies and mappings that need to be generated.
     *
     * ```
     * addDependenciesMapping(
     *  "androidx.compose.ui:ui" to "Compose.Ui",
     *  "androidx.appcompat:appcompat" to "Appcompat.B",
     * )
     * ```
     */
    public final void addDependenciesMapping(@org.jetbrains.annotations.NotNull()
    kotlin.Pair<java.lang.String, java.lang.String>... mappingDependencies) {
    }
    
    /**
     * Fetchs all the dependencies of a given [groups] from mvnrepository and add them.
     *
     * ```
     * addMvnDependencies(
     *  "org.springframework",
     *  "org.apache"
     * )
     * Fetch from:
     * https://mvnrepository.com/artifact/org.apache
     * https://mvnrepository.com/artifact/org.springframework
     * ```
     */
    public final void addMvnDependencies(@org.jetbrains.annotations.NotNull()
    java.lang.String... groups) {
    }
    
    /**
     * Applies the given [transformation] to transform the mapping target of the notation.
     *
     * ```
     * (Notation)                |    (Mapped field)
     * org.apache.cxf:cxf-api    |    Apache.Cxf.Api
     * org.mockito:android       |    Mockito.Android
     * com.google:guava          |    Guava
     * -------------------------------------------------
     * transformPath {
     *  it.removePrefix("org.")
     *    .removePrefix("com.google")
     * }
     * ```
     */
    public final void transformNotation(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.String> transformation) {
    }
    
    /**
     * Applies the given [transformation] to transform the generated name of classes or field.
     *
     * Note that each part of the path like `com.squareup.okio` is a name like `com` and `squareup` and `okio`.
     *
     * ```
     * transformName {
     *  when(it) {
     *    "androidx" -> "AndroidX"
     *    "activity" -> "Act"
     *    else -> it.replace("jetbrains", "jb")
     *  }
     * }
     * ```
     */
    public final void transformName(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.String> transformation) {
    }
    
    /**
     * Changes the path of output the generated jar with mapped dependencies to the specified [directory].
     *
     * Default, the jar file write out to the root project.
     *
     * @see jarName
     */
    public final void outputTo(@org.jetbrains.annotations.NotNull()
    java.io.File directory) {
    }
    
    /**
     * Changes the path of output the generated jar with mapped dependencies to the specified [directory].
     *
     * Default, the jar file write out to the root project.
     *
     * @see jarName
     */
    public final void outputTo(@org.jetbrains.annotations.NotNull()
    java.nio.file.Path directory) {
    }
    
    /**
     * When the predicate returns true, remapping the dependencies.
     *
     * By default, only when a new dependency ([addDependencies], [addMvnDependencies]) is manually added, will the
     * mapped dependency jar be regenerated. Please note that the change of [transformName] or [transformNotation] will
     * not be recorded. In this case, delete the jar manually and synchronize gradle.
     *
     * @see alwaysUpdate
     */
    public final void updateWhen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super org.gradle.api.Project, java.lang.Boolean> predicate) {
    }
    
    /**
     * Remappings dependencies whenever gradle sync.
     *
     * By default, only when a new dependency ([addDependencies], [addMvnDependencies]) is manually added, will the
     * mapped dependency jar be regenerated. Please note that the change of [transformName] or [transformNotation] will
     * not be recorded. In this case, delete the jar manually and synchronize gradle.
     *
     * @see updateWhen
     */
    public final void alwaysUpdate() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final DepFormatter getFormatter$mapper() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getDependencies$mapper() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getMvnGroups$mapper() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.String> getMappingDependencies$mapper() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final kotlin.jvm.functions.Function1<org.gradle.api.Project, java.lang.Boolean> getNeedUpdate$mapper() {
        return null;
    }
    
    public final void setNeedUpdate$mapper(@org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super org.gradle.api.Project, java.lang.Boolean> p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.io.File getOutputFile$mapper() {
        return null;
    }
    
    public final void setOutputFile$mapper(@org.jetbrains.annotations.Nullable()
    java.io.File p0) {
    }
}