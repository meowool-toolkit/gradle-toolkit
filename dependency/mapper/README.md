# 依赖类生成

将一系列的依赖声明转换为对应的类.


#### 介绍

-------

> 为依赖表达语 `foo.bar:pis` 生成以下代码
>
> ```java
> class Foo {
>   class Bar {
>     public static final String Pis = "com.foo.bar:pis:_";
>   }  
> }
> ```

## 动机

-------

能更方便、更优雅的在 `build.gradle(.kts)` 中导入期望的依赖。

**前：**

```kotlin
implementation("org.apache.commons:commons-lang3:_")
implementation("com.google.guava:guava:_")
implementation("com.squareup.okhttp3:okhttp:_")
```

**后：**

```kotlin
implementation(Libs.Google.Guava)
implementation(Libs.Apache.Commons.Lang3)
implementation(Libs.Square.OkHttp3)
```

以调用方式来导入依赖无疑更具有可读、可编辑性。

#### 依赖转换细节

-------

1. 转换为路径:

> `foo.bar:pis` -> `foo.bar.pis`

2. 如果组 ID 末端与工件 ID 前端相同则合并路径:

> `com.tt:tt` -> `com.tt`
>
> `foo.bar.gav:bar.gav` -> `foo.bar.gav`
>
> `vim.test-abc:test-abc` -> `vim.test.abc`
>
> `a.b:a.b` -> `a.b`

3. 路径上的每个名称都视为一个类，最后的名称则视为一个字段（即工件）。
   依赖路径的概念和文件路径的概念相似，上一个路径名称即为下一个路径名称的父亲。
   所有依赖都会合并成一棵树后添加到 `Libs` 类中:

> `foo.bar:egg` `foo.bar:ham` `foo.bar.gav:pizza`
> `meat.beef:steak` `meat.beef.steak:all`
>
> ```java
> // 默认大写所有成员名
> class Libs { 
>   class Foo { 
>     class Bar { 
>       public static final String Egg = "foo.bar:egg:_";
>       public static final String Ham = "foo.bar:ham:_";
>     }
>   }
>   class Meat { 
>     class Beef { 
>       public static final String Steak = "meat.beef:steak:_";
>
>       class Steak { 
>         public static final String All = "meat.beef.steak:all:_";
>       }
>     }
>   }
> }
> ```
