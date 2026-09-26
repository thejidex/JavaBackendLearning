# Java NIO：Path、Paths 与 Files 学习笔记

## 一、这一节到底在学什么？

Java NIO 中处理文件和目录时，最核心的两个类是：

- `Path`
- `Files`

可以先记住一句话：

> `Path` 负责表示“文件或目录在哪里”，`Files` 负责“对这个文件或目录做什么”。

例如：

```java
Path path = Path.of("abc.txt");
```

这里只是表示一个路径：

```text
abc.txt
```

此时硬盘上的 `abc.txt` 可以存在，也可以不存在。

真正去操作文件系统，需要使用 `Files`：

```java
Files.exists(path);
Files.createFile(path);
Files.delete(path);
```

所以可以这样理解：

```text
Path  = 地址
Files = 根据地址干活
```

---

## 二、Path 是什么？

`Path` 表示一个文件系统路径。

例如：

```java
Path path = Path.of("abc.txt");
```

或者：

```java
Path path = Path.of("docs", "java", "hello.txt");
```

第二种写法表示：

```text
docs
└── java
    └── hello.txt
```

需要特别注意：

```java
Path path = Path.of("abc.txt");
```

**不会创建 `abc.txt` 文件。**

这里只是在 Java 内存中创建了一个 `Path` 对象。

它表达的是：

```text
“我指的是 abc.txt 这个位置。”
```

至于这个位置有没有文件，`Path` 本身并不负责。

---

## 三、Path 和以前的 File 有什么关系？

以前学习 Java IO 时，我们经常写：

```java
File file = new File("abc.txt");
```

现在 NIO 中可以写：

```java
Path path = Path.of("abc.txt");
```

它们都可以表示文件路径。

区别在于旧 IO 的 `File` 同时承担了：

```text
表示路径
+
部分文件操作
```

而 NIO 将职责拆开了：

```text
Path
负责路径

Files
负责文件操作
```

因此 NIO 的设计更加清晰。

可以粗略理解为：

```text
旧 IO：

File
 ├─ 表示路径
 └─ 操作文件


NIO：

Path          Files
 ↓              ↓
表示路径        操作文件
```

---

## 四、Paths 是什么？

有些教程会写：

```java
Path path = Paths.get("abc.txt");
```

`Paths` 是一个工具类，主要作用是创建 `Path`。

也就是：

```text
字符串路径
   ↓
Paths.get()
   ↓
Path
```

不过现在更推荐直接写：

```java
Path path = Path.of("abc.txt");
```

所以：

```java
Paths.get("abc.txt");
```

和：

```java
Path.of("abc.txt");
```

在我们目前学习阶段，可以认为作用基本一样。

以后自己写代码时，可以优先使用：

```java
Path.of(...)
```

---

## 五、相对路径和绝对路径

### 1. 相对路径

例如：

```java
Path path = Path.of("abc.txt");
```

这里没有：

```text
C:\
D:\
```

因此它是相对路径。

那么：

```text
abc.txt
```

到底相对于哪里？

答案是：

> 当前 Java 程序的工作目录。

可以通过：

```java
System.out.println(System.getProperty("user.dir"));
```

查看。

假设输出：

```text
D:\Code\coding_everyday
```

那么：

```java
Path.of("abc.txt");
```

实际对应：

```text
D:\Code\coding_everyday\abc.txt
```

这也是为什么以前写：

```java
new File("abc.txt");
```

文件经常出现在 IDEA 项目根目录，而不是 `Main.java` 所在的 `src` 目录。

Java 看的是：

```text
当前工作目录
```

而不是：

```text
.java 文件所在目录
```

### 2. 绝对路径

例如：

```java
Path path = Path.of("D:\\Code\\abc.txt");
```

这是绝对路径。

可以使用：

```java
path.isAbsolute();
```

判断：

```java
Path path1 = Path.of("abc.txt");
Path path2 = Path.of("D:\\Code\\abc.txt");

System.out.println(path1.isAbsolute());
System.out.println(path2.isAbsolute());
```

结果：

```text
false
true
```

---

## 六、toAbsolutePath()

可以把相对路径转换成绝对路径：

```java
Path path = Path.of("abc.txt");

System.out.println(path.toAbsolutePath());
```

假设当前工作目录是：

```text
D:\Code\coding_everyday
```

那么结果可能是：

```text
D:\Code\coding_everyday\abc.txt
```

需要注意：

> `toAbsolutePath()` 只是计算路径，不会创建文件。

---

## 七、Path 的常用方法

假设：

```java
Path path = Path.of("docs", "java", "hello.txt");
```

表示：

```text
docs
└── java
    └── hello.txt
```

### 1. getFileName()

```java
path.getFileName();
```

结果：

```text
hello.txt
```

也就是：

> 获取路径最后一部分。

### 2. getParent()

```java
path.getParent();
```

结果：

```text
docs\java
```

也就是获取父路径。

### 3. getRoot()

如果：

```java
Path path = Path.of("D:\\Code\\hello.txt");
```

那么：

```java
path.getRoot();
```

结果可能是：

```text
D:\
```

因为：

```text
D:\
```

是这个路径的根。

但如果：

```java
Path path = Path.of("docs", "hello.txt");
```

这是相对路径，没有盘符，因此：

```java
path.getRoot();
```

通常得到：

```text
null
```

---

## 八、resolve()：路径拼接

`resolve()` 是这一节非常重要的方法。

可以直接理解为：

> 在当前路径后面继续拼接路径。

例如：

```java
Path path = Path.of("docs");

Path result = path.resolve("java");
```

结果：

```text
docs\java
```

继续：

```java
Path result = Path.of("docs")
        .resolve("java")
        .resolve("hello.txt");
```

得到：

```text
docs\java\hello.txt
```

所以：

```java
resolve()
```

可以记忆为：

```text
路径拼接
```

### 一个很重要的注意点

假设：

```java
Path path = Path.of("docs", "hello.txt");
```

然后：

```java
Path result = path.resolve("abc");
```

结果可能是：

```text
docs\hello.txt\abc
```

你可能觉得：

```text
hello.txt 不是文件吗？
为什么还能继续往后拼？
```

原因是：

> `Path` 只负责表示路径，并不知道 `hello.txt` 到底是不是一个真正存在的文件。

`.txt` 只是文件名的一部分。

Path 不会看到 `.txt` 就自动判断：

```text
“这是文件，后面不能再拼东西了。”
```

因此 `resolve()` 单纯负责路径拼接。

---

## 九、normalize()：整理路径

假设：

```java
Path path =
        Path.of("docs/java/../python/./hello.txt");
```

这里：

```text
.
```

表示当前目录。

```text
..
```

表示上一级目录。

执行：

```java
Path result = path.normalize();
```

结果类似：

```text
docs\python\hello.txt
```

因为：

```text
java/..
```

表示：

```text
进入 java
然后马上退回上一层
```

相当于抵消。

而：

```text
./
```

表示当前目录，也可以省略。

因此：

```text
docs/java/../python/./hello.txt
```

会被整理成：

```text
docs/python/hello.txt
```

需要注意：

> `normalize()` 只是整理路径，不会真的访问硬盘。

即使目录不存在：

```java
Path.of("不存在/a/../b.txt").normalize();
```

依然可以正常运行。

---

## 十、relativize()：计算相对路径

可以把：

```java
A.relativize(B);
```

理解成：

> 从 A 出发，怎么走才能走到 B？

例如：

```java
Path a = Path.of("D:\\project");
Path b = Path.of("D:\\project\\src\\Main.java");

System.out.println(a.relativize(b));
```

结果：

```text
src\Main.java
```

意思就是：

```text
当前：

D:\project

目标：

D:\project\src\Main.java

从当前走过去：

src\Main.java
```

再例如：

```text
A：

D:\project\src

B：

D:\project\test
```

那么：

```java
A.relativize(B);
```

可能得到：

```text
..\test
```

因为需要：

```text
src
 ↓
..
 ↓
project
 ↓
test
```

所以记住：

```text
resolve()
路径拼接

relativize()
从 A 到 B 怎么走
```

---

## 十一、Path 阶段的核心总结

到这里，Path 最重要的方法有：

```text
Path.of(...)
创建 Path

getFileName()
最后一部分

getParent()
父路径

getRoot()
根路径

isAbsolute()
是不是绝对路径

toAbsolutePath()
转换成绝对路径

resolve()
拼接路径

normalize()
整理路径

relativize()
计算两个路径之间的相对路径
```

Path 最大的特点是：

> 大部分操作只是在处理“路径”，并没有真正读写硬盘。

---

## 十二、Files 是什么？

真正操作文件系统的是：

```java
java.nio.file.Files
```

例如：

```java
Path path = Path.of("abc.txt");
```

这个只是表示：

```text
abc.txt
```

然后：

```java
Files.exists(path);
```

表示：

```text
这个位置有没有东西？
```

```java
Files.createFile(path);
```

表示：

```text
在这个位置创建文件。
```

```java
Files.delete(path);
```

表示：

```text
删除这个位置的文件。
```

因此：

```text
Path
回答：在哪里？

Files
回答：要干什么？
```

---

## 十三、Files.exists()

判断文件或者目录是否存在：

```java
Path path = Path.of("abc.txt");

boolean exists = Files.exists(path);

System.out.println(exists);
```

存在：

```text
true
```

不存在：

```text
false
```

---

## 十四、Files.createFile()

创建文件：

```java
Path path = Path.of("abc.txt");

Files.createFile(path);
```

这个时候才会真正创建：

```text
abc.txt
```

### 父目录不存在怎么办？

例如：

```java
Path path = Path.of("aaa", "bbb", "abc.txt");

Files.createFile(path);
```

如果：

```text
aaa
bbb
```

都不存在，会报错。

因为：

> `createFile()` 只负责创建文件，不会自动创建父目录。

所以通常可以先：

```java
Files.createDirectories(path.getParent());
Files.createFile(path);
```

---

## 十五、createDirectory() 和 createDirectories()

这是一个特别容易混淆的地方。

### createDirectory()

```java
Files.createDirectory(path);
```

只能创建当前这一层目录。

例如：

```text
aaa
```

已经存在：

```java
Files.createDirectory(Path.of("aaa", "bbb"));
```

可以创建：

```text
aaa
└── bbb
```

但是如果：

```text
aaa
```

都不存在：

```java
Files.createDirectory(
        Path.of("aaa", "bbb", "ccc")
);
```

就会失败。

### createDirectories()

```java
Files.createDirectories(
        Path.of("aaa", "bbb", "ccc")
);
```

会逐层创建：

```text
aaa
└── bbb
    └── ccc
```

因此实际开发中：

```java
Files.createDirectories(...)
```

会更加常用。

可以记：

```text
createDirectory

只创建一层


createDirectories

缺几层就补几层
```

---

## 十六、Files.copy()

复制文件：

```java
Files.copy(
        Path.of("a.txt"),
        Path.of("b.txt")
);
```

相当于：

```text
a.txt
 ↓
复制
 ↓
b.txt
```

如果 `b.txt` 已经存在，默认可能报错。

可以：

```java
Files.copy(
        Path.of("a.txt"),
        Path.of("b.txt"),
        StandardCopyOption.REPLACE_EXISTING
);
```

表示：

```text
目标文件已经存在
↓
直接覆盖
```

---

## 十七、Files.move()

移动文件：

```java
Files.move(
        Path.of("a.txt"),
        Path.of("folder", "a.txt")
);
```

相当于：

```text
a.txt

↓

folder
└── a.txt
```

### move 也可以用于重命名

例如：

```java
Files.move(
        Path.of("old.txt"),
        Path.of("new.txt")
);
```

效果相当于：

```text
old.txt
 ↓
new.txt
```

所以：

```text
移动文件
```

和：

```text
修改文件名
```

本质上都可以看成：

> 把文件从一个路径移动到另一个路径。

---

## 十八、Files.delete()

删除文件：

```java
Files.delete(
        Path.of("abc.txt")
);
```

如果文件不存在，会抛出异常。

如果不确定文件是否存在，可以：

```java
Files.deleteIfExists(
        Path.of("abc.txt")
);
```

如果存在就删除，不存在就算了。

---

## 十九、Files 和以前学习的 IO 流是什么关系？

以前读取文本文件时，可能需要：

```java
FileReader
BufferedReader
readLine()
```

例如：

```java
try (BufferedReader reader =
             new BufferedReader(
                     new FileReader("abc.txt"))) {

    String line;

    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
}
```

现在可以直接：

```java
List<String> lines =
        Files.readAllLines(
                Path.of("abc.txt")
        );
```

或者：

```java
String content =
        Files.readString(
                Path.of("abc.txt")
        );
```

可以直接读取整个文件。

所以可以理解为：

```text
以前：

FileInputStream
InputStreamReader
BufferedReader
...

很多步骤自己组合


现在：

Files.readString()
Files.readAllLines()

Java 帮我们封装好了
```

但是：

> `Files` 并不是取代 IO 流。

`Files` 更像是：

```text
方便使用的高级 API
```

而：

```text
InputStream
OutputStream
Reader
Writer
Channel
Buffer
```

仍然是理解 IO 原理的重要基础。

尤其是：

```text
大文件
网络 IO
流式处理
NIO 网络编程
```

仍然要理解流和 Channel。

---

## 二十、Files.readString()

读取整个文本文件：

```java
String content =
        Files.readString(
                Path.of("abc.txt")
        );

System.out.println(content);
```

例如文件：

```text
hello
world
```

那么 `content` 中就是：

```text
hello
world
```

---

## 二十一、Files.readAllLines()

读取所有行：

```java
List<String> lines =
        Files.readAllLines(
                Path.of("abc.txt")
        );
```

例如文件内容：

```text
aaa
bbb
ccc
```

那么：

```java
lines.get(0);
```

得到：

```text
aaa
```

```java
lines.get(1);
```

得到：

```text
bbb
```

---

## 二十二、Files.writeString()

写入字符串：

```java
Files.writeString(
        Path.of("abc.txt"),
        "Hello Java"
);
```

如果文件不存在，通常会创建文件。

如果文件已经存在，默认会覆盖原来的内容。

例如原本：

```text
AAAAA
```

执行：

```java
Files.writeString(
        Path.of("abc.txt"),
        "Hello"
);
```

之后变成：

```text
Hello
```

而不是：

```text
AAAAAHello
```

---

## 二十三、StandardOpenOption 是什么？

`StandardOpenOption` 用来告诉 Java：

> 这次打开文件时采用什么模式？

常见选项包括：

```text
READ
读取

WRITE
写入

APPEND
追加

CREATE
不存在就创建

CREATE_NEW
必须创建一个新文件，如果已经存在则报错

TRUNCATE_EXISTING
如果文件存在，清空原内容
```

例如追加内容：

```java
Files.writeString(
        Path.of("abc.txt"),
        "Hello\n",
        StandardOpenOption.APPEND
);
```

假设原文件：

```text
aaa
bbb
```

执行后：

```text
aaa
bbb
Hello
```

因此：

```java
StandardOpenOption.APPEND
```

可以理解为：

> 不覆盖旧内容，在文件末尾继续写。

---

## 二十四、walkFileTree()：遍历目录

`Files.walkFileTree()` 用来递归遍历目录。

例如目录：

```text
docs
├── a.txt
├── b.txt
├── java
│   ├── c.txt
│   └── d.txt
└── python
    └── e.txt
```

以前我们可能自己写递归：

```java
void dfs(File file) {

    if (file.isFile()) {
        System.out.println(file);
        return;
    }

    for (File child : file.listFiles()) {
        dfs(child);
    }
}
```

而 NIO 中：

```java
Files.walkFileTree(...)
```

已经帮我们把“递归遍历目录”的框架准备好了。

我们只需要告诉 Java：

> 遇到文件或目录时，我想干什么。

---

## 二十五、FileVisitor 的四个重要方法

遍历目录时，常见四个方法：

```java
preVisitDirectory(...)
visitFile(...)
visitFileFailed(...)
postVisitDirectory(...)
```

假设目录：

```text
docs
├── a.txt
└── java
    └── b.txt
```

遍历过程可以理解为：

```text
准备进入 docs
↓
preVisitDirectory(docs)

遇到 a.txt
↓
visitFile(a.txt)

准备进入 java
↓
preVisitDirectory(java)

遇到 b.txt
↓
visitFile(b.txt)

java 遍历结束
↓
postVisitDirectory(java)

docs 遍历结束
↓
postVisitDirectory(docs)
```

如果访问某个文件失败：

```text
没权限
文件损坏
访问异常
```

则调用：

```java
visitFileFailed(...)
```

所以可以直接记：

```text
preVisitDirectory
进入目录之前

visitFile
访问到一个文件

visitFileFailed
访问失败

postVisitDirectory
目录遍历完成
```

---

## 二十六、SimpleFileVisitor 是什么？

`FileVisitor` 本身要求实现多个方法。

为了方便，Java 提供了：

```java
SimpleFileVisitor<Path>
```

它已经给这些方法提供了默认实现。

因此我们只需要重写自己关心的方法。

例如只打印文件：

```java
Files.walkFileTree(
        Path.of("docs"),
        new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(
                    Path file,
                    BasicFileAttributes attrs) {

                System.out.println(file);

                return FileVisitResult.CONTINUE;
            }
        }
);
```

这样就不用把四个方法全部写一遍。

---

## 二十七、FileVisitResult 是什么？

它用来告诉 Java：

> 接下来遍历还要不要继续？

例如：

```java
return FileVisitResult.CONTINUE;
```

表示：

```text
继续遍历
```

而：

```java
return FileVisitResult.TERMINATE;
```

表示：

```text
立刻结束整个遍历
```

这在“搜索文件”时很好用。

例如：

```java
if (file.getFileName().toString().equals("target.txt")) {
    System.out.println("找到文件：" + file);
    return FileVisitResult.TERMINATE;
}
```

找到目标文件后，就没必要继续遍历了。

---

# 二十八、整节知识的核心关系

最终可以把这一节压缩成：

```text
                 Java NIO 文件操作
                        │
            ┌───────────┴───────────┐
            │                       │
           Path                   Files
            │                       │
          路径                    操作文件
            │                       │
      Path.of(...)             exists()
      getParent()              createFile()
      getFileName()            createDirectories()
      getRoot()                copy()
      resolve()                move()
      normalize()              delete()
      relativize()             readString()
                               readAllLines()
                               writeString()
                               walkFileTree()
```

最重要的一句话：

> `Path` 负责“在哪里”，`Files` 负责“干什么”。

---

# 二十九、几个最容易搞混的地方

### 1. Path.of() 会不会创建文件？

不会。

```java
Path.of("abc.txt");
```

只创建一个 Java 路径对象。

真正创建文件：

```java
Files.createFile(path);
```

---

### 2. Path 为什么可以表示不存在的文件？

因为 `Path` 只是地址。

就像你可以写：

```text
火星市幸福路 100 号
```

这个地址就算现实中不存在，你依然可以把它写出来。

Path 也是如此。

---

### 3. resolve() 会不会检查路径真的存在？

不会。

它只是拼接路径。

---

### 4. normalize() 会不会访问磁盘？

不会。

它只是整理：

```text
.
..
```

这些路径结构。

---

### 5. Files.createFile() 会不会自动创建父目录？

不会。

所以：

```java
Files.createDirectories(path.getParent());
Files.createFile(path);
```

经常一起使用。

---

### 6. createDirectory() 和 createDirectories() 的区别？

```text
createDirectory
只创建当前一层

createDirectories
父目录不存在时一起创建
```

---

### 7. Files 和 IO 流谁更重要？

都重要。

```text
Files
适合方便地完成普通文件操作

InputStream / Reader / Channel / Buffer
适合理解 IO 原理以及处理更复杂的 IO 场景
```

不能因为有 `Files.readString()` 就认为以前学的流没用了。

---

# 三十、目前学习阶段建议掌握的 API

目前不需要背完整个 `Files` 类。

优先熟练下面这些：

```java
Path.of(...)

path.getFileName()
path.getParent()
path.toAbsolutePath()

path.resolve(...)
path.normalize()
path.relativize(...)

Files.exists(...)

Files.createFile(...)
Files.createDirectory(...)
Files.createDirectories(...)

Files.copy(...)
Files.move(...)

Files.delete(...)
Files.deleteIfExists(...)

Files.readString(...)
Files.readAllLines(...)
Files.writeString(...)

Files.walkFileTree(...)
```

看到它们能知道在做什么，就已经足够应付目前的 Java NIO 学习。

---

# 三十一、最终记忆口诀

可以用下面这几句话快速复习：

```text
Path 是地址，Files 是干活。

Path.of 只造地址，不造文件。

resolve 是拼路径。

normalize 是整理 ./ ../。

relativize 是算“从 A 到 B 怎么走”。

createFile 只建文件，不建父目录。

createDirectory 只建一层。

createDirectories 缺几层补几层。

copy 是复制，move 是移动或改名。

walkFileTree 本质就是目录 DFS。
```

如果以后忘了这一节，重新看这一段基本就能把整个知识体系恢复起来。
