# Java NIO：Path、Paths、Files 与文件遍历学习笔记

> 目标：不是背 API，而是学会**看懂这些类和方法的设计，并知道在什么场景下怎么调用**。
>
> 适合阶段：已经学过 Java 基础、异常、IO 流，正在学习 NIO。

---

# 0. 先建立整体认识：这一节到底有哪些东西？

第一次看这一节容易懵，是因为 Java 一口气出现了很多类型：

```text
Path
Paths
Files
LinkOption
FileAttribute
StandardCopyOption
StandardOpenOption
BasicFileAttributes
FileVisitor
SimpleFileVisitor
FileVisitResult
```

不要把它们看成十几个互不相关的新知识。

实际上可以分成 5 组：

```text
① 路径
Path、Paths

② 文件操作
Files

③ 操作时的“附加选项”
LinkOption
StandardCopyOption
StandardOpenOption
FileAttribute

④ 文件属性
BasicFileAttributes

⑤ 目录递归遍历
FileVisitor
SimpleFileVisitor
FileVisitResult
```

最核心的关系只有一句话：

> **Path 负责表示“在哪里”，Files 负责“干什么”，其他类型大多是给 Files 提供额外参数。**

例如：

```java
Path source = Path.of("a.txt");
Path target = Path.of("b.txt");

Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
```

这里：

```text
source / target
    ↓
Path：文件在哪里

Files.copy(...)
    ↓
Files：我要执行复制

REPLACE_EXISTING
    ↓
StandardCopyOption：如果目标已经存在怎么办
```

把这个关系搞清楚以后，后面的类就不会显得那么散了。

---

# 1. Path：专门表示“路径”

## 1.1 Path 到底是什么？

`Path` 是一个接口，用来表示文件系统中的一个路径。

例如：

```java
Path path = Path.of("abc.txt");
```

这里只是在 Java 内存中创建了一个路径对象。

**不会创建真正的 `abc.txt` 文件。**

可以把它想成：

```text
Path = 地址
```

比如你在纸上写：

```text
北京市某某路 100 号
```

不代表那里一定真的有一栋房子。

同理：

```java
Path path = Path.of("D:\\abc\\hello.txt");
```

只是表达：

> “我现在说的是这个位置。”

至于这个文件到底存不存在，需要 `Files` 去检查。

---

# 2. Path.of()：创建 Path

现在写 Java，推荐直接使用：

```java
Path path = Path.of("abc.txt");
```

还可以写：

```java
Path path = Path.of("docs", "java", "hello.txt");
```

它相当于组合成：

```text
docs/java/hello.txt
```

Windows 上打印时通常显示：

```text
docs\java\hello.txt
```

## 方法怎么读？

看到：

```java
Path.of(String first, String... more)
```

不要怕。

拆开看：

```text
Path
↑
返回值：调用以后得到一个 Path

of
↑
方法名

String first
↑
至少给一个字符串

String... more
↑
后面还可以继续给任意多个字符串
```

所以：

```java
Path.of("a", "b", "c.txt");
```

完全合法。

---

# 3. Paths：以前常用的 Path 工厂类

教程经常写：

```java
Path path = Paths.get("abc.txt");
```

`Paths` 是工具类，核心作用就是：

```text
字符串
 ↓
Path
```

例如：

```java
Path path = Paths.get("docs", "hello.txt");
```

现在更推荐写：

```java
Path path = Path.of("docs", "hello.txt");
```

目前学习时，你可以把：

```java
Paths.get(...)
```

和：

```java
Path.of(...)
```

看成基本同一类用途。

看到旧代码里的 `Paths.get()` 要认识，但自己写代码时优先 `Path.of()` 即可。

---

# 4. 相对路径和绝对路径

## 4.1 相对路径

```java
Path path = Path.of("abc.txt");
```

这是相对路径。

它相对于谁？

相对于 Java 程序当前的工作目录：

```java
System.out.println(System.getProperty("user.dir"));
```

假设打印：

```text
D:\Code\coding_everyday
```

那么：

```java
Path.of("abc.txt");
```

对应的位置就是：

```text
D:\Code\coding_everyday\abc.txt
```

这和 `.java` 源文件放在哪个目录没有直接关系。

---

## 4.2 绝对路径

```java
Path path = Path.of("D:\\Code\\abc.txt");
```

这是绝对路径。

可以判断：

```java
boolean result = path.isAbsolute();
```

例如：

```java
Path a = Path.of("abc.txt");
Path b = Path.of("D:\\Code\\abc.txt");

System.out.println(a.isAbsolute()); // false
System.out.println(b.isAbsolute()); // true
```

### `isAbsolute()` 怎么读？

```java
boolean isAbsolute()
```

表示：

```text
不需要参数
↓
判断当前 Path 是不是绝对路径
↓
返回 boolean
```

这种方法以后你看到 `isXXX()`，通常就可以猜到：

> 它大概率是在做判断，返回 `boolean`。

---

# 5. Path 常用方法详解

先准备：

```java
Path path = Path.of("docs", "java", "hello.txt");
```

它表示：

```text
docs
└── java
    └── hello.txt
```

---

## 5.1 getFileName()：拿最后一部分

```java
Path fileName = path.getFileName();
System.out.println(fileName);
```

结果：

```text
hello.txt
```

注意返回的是：

```java
Path
```

不是 `String`。

如果你需要字符串：

```java
String fileName = path.getFileName().toString();
```

实际开发中很常见：

```java
if (path.getFileName().toString().equals("Main.java")) {
    // 找到了 Main.java
}
```

---

## 5.2 getParent()：拿父路径

```java
Path parent = path.getParent();
System.out.println(parent);
```

结果：

```text
docs\java
```

常见用途之一：创建文件之前先确保父目录存在。

```java
Path file = Path.of("logs", "2026", "app.log");

Files.createDirectories(file.getParent());
Files.createFile(file);
```

这里：

```java
file.getParent()
```

得到：

```text
logs/2026
```

然后先创建目录，再创建文件。

### 小心 `null`

例如：

```java
Path path = Path.of("abc.txt");
System.out.println(path.getParent());
```

对于这种只有一个名字的相对路径，`getParent()` 可能是 `null`。

所以实际开发中不能永远想当然地：

```java
Files.createDirectories(path.getParent());
```

如果路径来源不确定，最好先判断。

---

## 5.3 getRoot()：获取根路径

```java
Path path = Path.of("D:\\Code\\hello.txt");
System.out.println(path.getRoot());
```

Windows 上通常得到：

```text
D:\
```

如果是相对路径：

```java
Path path = Path.of("docs", "hello.txt");
```

那么：

```java
path.getRoot()
```

通常是：

```text
null
```

---

## 5.4 toAbsolutePath()：得到绝对路径

```java
Path path = Path.of("abc.txt");
Path absolute = path.toAbsolutePath();

System.out.println(absolute);
```

假设工作目录是：

```text
D:\Code\coding_everyday
```

那么可能得到：

```text
D:\Code\coding_everyday\abc.txt
```

注意：

> `toAbsolutePath()` 不会创建文件，只是在计算路径。

实际排查“文件到底读到哪里去了”时非常有用：

```java
System.out.println("实际读取路径：" + path.toAbsolutePath());
```

---

## 5.5 resolve()：路径拼接

这是非常常用的方法。

```java
Path base = Path.of("docs");
Path result = base.resolve("java");

System.out.println(result);
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

记忆：

```text
A.resolve(B)
≈
把 B 接到 A 后面
```

### 非常重要的坑

```java
Path path = Path.of("docs", "hello.txt");
Path result = path.resolve("abc");
```

仍然可以得到：

```text
docs\hello.txt\abc
```

为什么？

因为 `Path` 只处理路径结构，它不会因为名字以 `.txt` 结尾就认定它一定是文件。

---

## 5.6 resolveSibling()：在“同一级”换一个路径

例如：

```java
Path path = Path.of("docs", "a.txt");
Path result = path.resolveSibling("b.txt");
```

得到：

```text
docs\b.txt
```

可以理解成：

```text
docs/a.txt
       ↓
把 a.txt 换成同级的 b.txt
       ↓
docs/b.txt
```

实际用途：生成同目录下的新文件名。

```java
Path source = Path.of("images", "avatar.jpg");
Path backup = source.resolveSibling("avatar_backup.jpg");
```

---

## 5.7 normalize()：整理 `.` 和 `..`

```java
Path path = Path.of("docs/java/../python/./hello.txt");
Path normalized = path.normalize();

System.out.println(normalized);
```

结果类似：

```text
docs\python\hello.txt
```

因为：

```text
java/..
```

相互抵消。

而：

```text
./
```

表示当前目录，可以省略。

注意：

> `normalize()` 只是做路径层面的整理，不访问硬盘。

所以：

```java
Path.of("一个不存在的目录/a/../b.txt").normalize();
```

也能正常运行。

---

## 5.8 relativize()：计算“从 A 到 B 怎么走”

```java
Path a = Path.of("D:\\project");
Path b = Path.of("D:\\project\\src\\Main.java");

Path result = a.relativize(b);
System.out.println(result);
```

结果：

```text
src\Main.java
```

记忆：

```text
A.relativize(B)
=
从 A 出发怎么走到 B
```

再比如：

```text
A = D:\project\src
B = D:\project\test
```

则：

```java
A.relativize(B)
```

可能得到：

```text
..\test
```

---

## 5.9 startsWith() / endsWith()：判断路径开头和结尾

```java
Path path = Path.of("docs", "java", "Main.java");

System.out.println(path.startsWith("docs"));
System.out.println(path.endsWith("Main.java"));
```

常用于路径筛选。

注意它判断的是**路径元素**，不是普通字符串的模糊匹配。

---

## 5.10 getNameCount() / getName()

```java
Path path = Path.of("docs", "java", "Main.java");

System.out.println(path.getNameCount());
```

结果：

```text
3
```

然后：

```java
System.out.println(path.getName(0)); // docs
System.out.println(path.getName(1)); // java
System.out.println(path.getName(2)); // Main.java
```

这些方法不算最常用，但能帮助你理解：

> `Path` 不是单纯存了一个字符串，而是把路径理解成一段一段的路径元素。

---

## 5.11 toFile()：与旧 IO 的 File 互转

如果某个老 API 只接受 `File`：

```java
Path path = Path.of("abc.txt");
File file = path.toFile();
```

反过来：

```java
File file = new File("abc.txt");
Path path = file.toPath();
```

这是新旧 IO API 之间的桥梁。

---

# 6. Files：真正操作文件系统的工具类

`Files` 是一个工具类，大量方法都是：

```java
static
```

所以通常不需要：

```java
new Files(); // 不这么用
```

而是直接：

```java
Files.exists(...);
Files.copy(...);
Files.delete(...);
```

可以把它理解成：

```text
Path 负责地址
Files 负责执行动作
```

---

# 7. 学 Files 方法时，先学会看方法签名

例如教程写：

```java
exists(Path path, LinkOption... options)
```

你可能第一眼就烦了。

其实分开看：

```text
exists
方法名

Path path
必须告诉它检查哪个路径

LinkOption... options
可选参数，可以传 0 个、1 个甚至多个额外选项
```

所以最普通的调用就是：

```java
Files.exists(path);
```

因为 `options` 是可变参数，可以一个都不传。

以后看到：

```java
XXX... args
```

要马上意识到：

> “这个参数可以不传，也可以传多个。”

---

# 8. Files.exists()：判断是否存在

```java
Path path = Path.of("abc.txt");

boolean exists = Files.exists(path);
```

完整例子：

```java
if (Files.exists(path)) {
    System.out.println("文件存在");
} else {
    System.out.println("文件不存在");
}
```

### 典型用途

文件操作之前先判断：

```java
Path config = Path.of("config.json");

if (!Files.exists(config)) {
    System.out.println("配置文件不存在");
}
```

### LinkOption 是干嘛的？

方法签名可能写成：

```java
Files.exists(Path path, LinkOption... options)
```

`LinkOption` 用来决定符号链接怎么处理。

初学阶段绝大多数时候：

```java
Files.exists(path);
```

即可。

看到 `LinkOption...` 不用慌，也不用为了调用这个方法硬塞一个参数进去。

---

# 9. Files.isRegularFile() / isDirectory()

判断是不是普通文件：

```java
if (Files.isRegularFile(path)) {
    System.out.println("这是普通文件");
}
```

判断是不是目录：

```java
if (Files.isDirectory(path)) {
    System.out.println("这是目录");
}
```

相比只判断：

```java
Files.exists(path)
```

这两个方法的信息更加具体。

例如：

```java
Path path = Path.of("data");

if (!Files.exists(path)) {
    System.out.println("路径不存在");
} else if (Files.isDirectory(path)) {
    System.out.println("这是目录");
} else if (Files.isRegularFile(path)) {
    System.out.println("这是普通文件");
}
```

---

# 10. Files.isReadable() / isWritable() / isExecutable()

```java
Files.isReadable(path);
Files.isWritable(path);
Files.isExecutable(path);
```

分别判断：

```text
是否可读
是否可写
是否可执行
```

例如：

```java
if (!Files.isReadable(path)) {
    System.out.println("这个文件无法读取");
}
```

在真正的服务器程序里，如果程序访问用户上传目录、配置目录、日志目录失败，这些方法有时可以帮助排查权限问题。

---

# 11. Files.createFile()：创建空文件

```java
Path path = Path.of("abc.txt");
Files.createFile(path);
```

创建的是一个空文件。

### 重要：文件已经存在会怎样？

会抛异常，例如：

```text
FileAlreadyExistsException
```

因此：

```java
if (!Files.exists(path)) {
    Files.createFile(path);
}
```

看起来可以避免一部分问题。

但实际并发程序中，“先判断再创建”仍可能发生竞争，所以真正需要“必须创建新文件”时，应依赖创建操作本身的异常语义，而不是认为 `exists()` 能提供并发安全保证。

现阶段你先知道：

> `createFile()` 不等于“确保这个文件存在”，它的语义是“创建一个新的文件”。

---

# 12. FileAttribute<?>：创建文件时顺便指定属性

你可能会看到：

```java
Files.createFile(Path path, FileAttribute<?>... attrs)
```

初学者最容易被：

```java
FileAttribute<?>... attrs
```

吓到。

实际上它是一个**可选参数**。

所以最普通的调用：

```java
Files.createFile(path);
```

完全没问题。

只有在 Linux / Unix 等环境下，你希望创建文件时直接指定权限等属性，才会用到它。

例如 POSIX 权限属于偏进阶内容。

目前学习优先级：

```text
知道它是什么：★★★
熟练使用：★
```

也就是说，现在**不需要背它的写法**。

---

# 13. Files.createDirectory()：创建一个目录

```java
Path dir = Path.of("data");
Files.createDirectory(dir);
```

如果父目录不存在，会失败。

例如：

```java
Files.createDirectory(Path.of("a", "b", "c"));
```

如果：

```text
a/b
```

不存在，那么不能直接创建 `c`。

---

# 14. Files.createDirectories()：递归创建目录

这个方法在实际开发中更加常用。

```java
Path dir = Path.of("a", "b", "c");
Files.createDirectories(dir);
```

如果原来一个都没有：

```text
a
└── b
    └── c
```

会自动一层一层补齐。

记忆：

```text
createDirectory
只创建一层

createDirectories
缺几层补几层
```

### 后端实际场景

例如用户上传头像：

```java
Path uploadDir = Path.of("uploads", "avatars");
Files.createDirectories(uploadDir);
```

之后再保存：

```java
Path target = uploadDir.resolve("10001.jpg");
```

---

# 15. Files.delete()：删除

```java
Files.delete(Path.of("abc.txt"));
```

如果目标不存在，会抛异常。

目录如果不是空目录，也通常不能直接删。

---

# 16. Files.deleteIfExists()：存在就删

```java
boolean deleted = Files.deleteIfExists(path);
```

返回：

```text
true  -> 确实删掉了东西
false -> 原本就不存在
```

如果你不关心“文件不存在”这种情况，通常比 `delete()` 更方便。

例如清理临时文件：

```java
Files.deleteIfExists(tempFile);
```

---

# 17. Files.copy()：复制

最基本：

```java
Path source = Path.of("a.txt");
Path target = Path.of("b.txt");

Files.copy(source, target);
```

如果 `b.txt` 已经存在，默认可能抛：

```text
FileAlreadyExistsException
```

所以经常写：

```java
Files.copy(
        source,
        target,
        StandardCopyOption.REPLACE_EXISTING
);
```

意思是：

> 目标已经存在就覆盖。

---

# 18. StandardCopyOption：copy/move 的附加选项

最常见两个：

## REPLACE_EXISTING

```java
StandardCopyOption.REPLACE_EXISTING
```

目标存在就覆盖。

## COPY_ATTRIBUTES

```java
StandardCopyOption.COPY_ATTRIBUTES
```

复制文件时尽量把属性也复制过去，例如时间戳等。

例如：

```java
Files.copy(
        source,
        target,
        StandardCopyOption.REPLACE_EXISTING,
        StandardCopyOption.COPY_ATTRIBUTES
);
```

注意这里再次出现了：

```java
CopyOption... options
```

因此可以传多个选项。

---

# 19. Files.move()：移动，也可以重命名

移动：

```java
Path source = Path.of("a.txt");
Path target = Path.of("data", "a.txt");

Files.move(source, target);
```

本质：

```text
a.txt
↓
data/a.txt
```

## 改名

```java
Files.move(
        Path.of("old.txt"),
        Path.of("new.txt")
);
```

为什么移动可以实现重命名？

因为从文件系统角度来看：

> 改名本质上也是从一个路径移动到另一个路径。

覆盖已存在目标：

```java
Files.move(
        source,
        target,
        StandardCopyOption.REPLACE_EXISTING
);
```

---

# 20. Files.size()：获取文件大小

```java
long size = Files.size(path);
```

单位是：

```text
字节 byte
```

例如：

```java
long size = Files.size(Path.of("photo.jpg"));
System.out.println("大小：" + size + " 字节");
```

如果想粗略转 KB：

```java
double kb = size / 1024.0;
```

这个方法对于上传文件限制、日志统计等后端场景很实用。

---

# 21. Files.readString()：一次读取整个文本文件

```java
Path path = Path.of("abc.txt");
String content = Files.readString(path);

System.out.println(content);
```

适合：

```text
配置文件
小型 JSON
小型文本
测试数据
```

不适合特别大的文件。

为什么？

因为它会把整个文件内容一次性放进内存。

专业开发中要形成这个意识：

> API 越方便，不代表越适合所有数据规模。

---

# 22. Files.readAllLines()：一次读取所有行

```java
List<String> lines = Files.readAllLines(path);
```

如果文件：

```text
AAA
BBB
CCC
```

那么：

```java
lines.get(0) // AAA
lines.get(1) // BBB
lines.get(2) // CCC
```

指定编码：

```java
List<String> lines = Files.readAllLines(
        path,
        StandardCharsets.UTF_8
);
```

适合小型文本。

同样，不适合几十 GB 的日志文件，因为所有行都会进入内存。

---

# 23. Files.writeString()：写字符串

```java
Path path = Path.of("abc.txt");

Files.writeString(path, "Hello Java");
```

如果文件不存在，通常会创建。

如果存在，默认会覆盖原内容。

例如原来：

```text
AAAA
```

执行：

```java
Files.writeString(path, "Hello");
```

之后：

```text
Hello
```

---

# 24. StandardOpenOption：告诉 Java “怎么打开文件”

`StandardOpenOption` 是一个枚举。

你可以把它理解成：

> 给写入/打开文件操作添加“模式”。

---

## 24.1 APPEND：追加

```java
Files.writeString(
        path,
        "Hello\n",
        StandardOpenOption.APPEND
);
```

不会覆盖原内容，而是在结尾继续写。

---

## 24.2 CREATE：没有就创建

```java
StandardOpenOption.CREATE
```

语义：

```text
不存在 -> 创建
存在 -> 使用现有文件
```

---

## 24.3 CREATE_NEW：必须是一个新文件

```java
StandardOpenOption.CREATE_NEW
```

语义：

```text
不存在 -> 创建成功
存在 -> 报 FileAlreadyExistsException
```

---

## 24.4 TRUNCATE_EXISTING：清空原内容

```java
StandardOpenOption.TRUNCATE_EXISTING
```

通常与写入模式配合。

含义：

> 文件存在的话，打开时先把长度截断到 0。

也就是清空。

---

## 24.5 READ / WRITE

```java
StandardOpenOption.READ
StandardOpenOption.WRITE
```

分别表示以读或写的方式打开。

很多高层 API 会替你默认处理，因此不是每次都要手动指定。

---

# 25. Files.write()：写多行文本

```java
List<String> lines = List.of(
        "AAA",
        "BBB",
        "CCC"
);

Files.write(
        Path.of("abc.txt"),
        lines,
        StandardCharsets.UTF_8
);
```

文件结果：

```text
AAA
BBB
CCC
```

如果你手上本来就有 `List<String>`，这个方法很方便。

---

# 26. Files.newBufferedReader()：用 NIO 创建 BufferedReader

以前你可能写：

```java
BufferedReader reader = new BufferedReader(
        new FileReader("abc.txt")
);
```

现在可以：

```java
Path path = Path.of("abc.txt");

try (BufferedReader reader = Files.newBufferedReader(
        path,
        StandardCharsets.UTF_8
)) {
    String line;

    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
}
```

这里非常值得理解：

> `Files` 并没有让以前学的 `BufferedReader` 过时，它只是提供了一种更方便的创建方式。

如果文件比较大，又需要逐行处理，这通常比 `readAllLines()` 更合适。

为什么？

```text
readAllLines()
一次把所有内容塞进内存

BufferedReader
一行一行读
```

---

# 27. Files.newBufferedWriter()：创建 BufferedWriter

```java
Path path = Path.of("abc.txt");

try (BufferedWriter writer = Files.newBufferedWriter(
        path,
        StandardCharsets.UTF_8
)) {
    writer.write("Hello");
    writer.newLine();
    writer.write("Java");
}
```

写入：

```text
Hello
Java
```

如果要追加：

```java
try (BufferedWriter writer = Files.newBufferedWriter(
        path,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND
)) {
    writer.write("new log");
    writer.newLine();
}
```

这就是日志追加的基本思想之一。

---

# 28. Files.newInputStream() / newOutputStream()

如果你想继续使用自己熟悉的字节流，也可以从 `Path` 创建。

读取：

```java
try (InputStream in = Files.newInputStream(path)) {
    // 按字节读取
}
```

写入：

```java
try (OutputStream out = Files.newOutputStream(path)) {
    // 按字节写入
}
```

这能帮助你把前面学习的 IO 和现在的 NIO 文件 API 串起来：

```text
Path
 ↓
Files.newInputStream()
 ↓
InputStream
```

所以不要把 IO 和 NIO 当成两套互不相关的世界。

---

# 29. BasicFileAttributes：一次拿到文件的基本属性

当你使用 `walkFileTree()` 时，会不断看到：

```java
BasicFileAttributes attrs
```

这个接口表示文件的一些基本属性。

常用方法：

```java
attrs.size()
attrs.creationTime()
attrs.lastModifiedTime()
attrs.lastAccessTime()
attrs.isRegularFile()
attrs.isDirectory()
attrs.isSymbolicLink()
```

例如：

```java
@Override
public FileVisitResult visitFile(
        Path file,
        BasicFileAttributes attrs
) throws IOException {

    System.out.println("文件：" + file);
    System.out.println("大小：" + attrs.size());
    System.out.println("修改时间：" + attrs.lastModifiedTime());

    return FileVisitResult.CONTINUE;
}
```

记忆：

```text
file
=
当前文件在哪里

attrs
=
当前文件是什么情况
```

---

# 30. Files.readAttributes()：主动读取属性

不使用 `walkFileTree()` 时，也可以主动获取：

```java
BasicFileAttributes attrs = Files.readAttributes(
        path,
        BasicFileAttributes.class
);
```

然后：

```java
System.out.println(attrs.size());
System.out.println(attrs.creationTime());
System.out.println(attrs.lastModifiedTime());
```

这里的：

```java
BasicFileAttributes.class
```

可以理解成：

> “我要读取 `BasicFileAttributes` 这一套属性。”

这是 Java 反射/Class 对象相关语法的一个实际使用场景，目前先会用即可。

---

# 31. Files.list()：只遍历当前目录一层

假设：

```text
docs
├── a.txt
├── b.txt
└── java
    └── Main.java
```

写：

```java
try (Stream<Path> stream = Files.list(Path.of("docs"))) {
    stream.forEach(System.out::println);
}
```

只会看到：

```text
docs/a.txt
docs/b.txt
docs/java
```

不会继续进入 `java` 找 `Main.java`。

### 很重要：为什么 try-with-resources？

`Files.list()` 返回：

```java
Stream<Path>
```

这个 Stream 背后连接着目录资源，用完应该关闭。

所以推荐：

```java
try (Stream<Path> stream = Files.list(path)) {
    ...
}
```

而不是长期把它悬着。

---

# 32. Files.walk()：递归遍历，返回 Stream<Path>

```java
try (Stream<Path> stream = Files.walk(Path.of("docs"))) {
    stream.forEach(System.out::println);
}
```

它会递归向下遍历。

例如：

```text
docs
docs/a.txt
docs/java
docs/java/Main.java
```

和 `walkFileTree()` 相比：

```text
Files.walk()
适合：我要拿到一串 Path，然后 filter/map/forEach

Files.walkFileTree()
适合：我要精细控制进入目录、退出目录、访问失败、跳过子树等过程
```

例如查找所有 `.java`：

```java
try (Stream<Path> stream = Files.walk(Path.of("src"))) {
    stream
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().endsWith(".java"))
            .forEach(System.out::println);
}
```

这是现代 Java 项目里很常见的写法。

---

# 33. Files.walkFileTree()：Java 文件系统版 DFS

这是这一节最重要、也最容易懵的方法之一。

先把它翻译成人话：

> **从某个目录开始做深度优先遍历，并且在遍历到不同阶段时回调我提供的方法。**

算法视角：

```text
Files.walkFileTree()
≈
Java 官方帮你写好的目录树 DFS 框架
```

---

# 34. 最简单的 walkFileTree() 写法

```java
Path start = Path.of("D:\\test");

Files.walkFileTree(
        start,
        new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(
                    Path file,
                    BasicFileAttributes attrs
            ) throws IOException {

                System.out.println(file);
                return FileVisitResult.CONTINUE;
            }
        }
);
```

整段翻译：

```text
Files.walkFileTree(...)
遍历文件树

start
从哪里开始

new SimpleFileVisitor<Path>()
我来定义遍历时怎么处理

visitFile(...)
每遇到一个文件调用这里

file
当前文件

attrs
当前文件属性

CONTINUE
继续遍历
```

---

# 35. FileVisitor：遍历过程中有哪些“回调时机”？

`FileVisitor` 定义了 4 个主要方法：

```java
preVisitDirectory(...)
visitFile(...)
visitFileFailed(...)
postVisitDirectory(...)
```

不要把它们背成四个奇怪英文。

直接翻译：

```text
preVisitDirectory
进入目录之前

visitFile
遇到普通文件

visitFileFailed
访问文件失败

postVisitDirectory
这个目录已经遍历完，准备离开
```

---

# 36. SimpleFileVisitor：为什么一般不直接实现 FileVisitor？

如果直接：

```java
implements FileVisitor<Path>
```

那么四个方法都必须实现。

很多时候我只关心：

```java
visitFile()
```

所以 Java 提供：

```java
SimpleFileVisitor<Path>
```

它已经把 `FileVisitor` 的方法写了默认实现。

于是：

```java
new SimpleFileVisitor<Path>() {
    @Override
    public FileVisitResult visitFile(...) {
        ...
    }
}
```

只重写需要的方法就行。

这是一个很典型的面向对象设计思想：

```text
接口 FileVisitor
        ↓
默认实现 SimpleFileVisitor
        ↓
我们继承它，只覆盖关心的行为
```

---

# 37. walkFileTree() 的真实执行顺序

假设：

```text
test
├── a.txt
└── java
    └── Main.java
```

完整 visitor：

```java
Files.walkFileTree(
        Path.of("test"),
        new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(
                    Path dir,
                    BasicFileAttributes attrs
            ) throws IOException {
                System.out.println("进入：" + dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(
                    Path file,
                    BasicFileAttributes attrs
            ) throws IOException {
                System.out.println("文件：" + file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(
                    Path file,
                    IOException exc
            ) throws IOException {
                System.out.println("失败：" + file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(
                    Path dir,
                    IOException exc
            ) throws IOException {
                System.out.println("离开：" + dir);
                return FileVisitResult.CONTINUE;
            }
        }
);
```

执行逻辑大致是：

```text
进入 test
│
├─ 文件 a.txt
│
└─ 进入 java
      │
      ├─ 文件 Main.java
      │
      └─ 离开 java
│
└─ 离开 test
```

这就是标准 DFS 的感觉。

---

# 38. preVisitDirectory()：进入目录之前

签名：

```java
public FileVisitResult preVisitDirectory(
        Path dir,
        BasicFileAttributes attrs
) throws IOException
```

参数：

```text
dir
即将进入哪个目录

attrs
这个目录的基本属性
```

最常见作用：

```text
打印目录
跳过某些目录
进入目录前初始化状态
```

例如不进入 `.git`：

```java
@Override
public FileVisitResult preVisitDirectory(
        Path dir,
        BasicFileAttributes attrs
) throws IOException {

    if (dir.getFileName() != null
            && dir.getFileName().toString().equals(".git")) {
        return FileVisitResult.SKIP_SUBTREE;
    }

    return FileVisitResult.CONTINUE;
}
```

---

# 39. visitFile()：真正访问到一个文件

```java
@Override
public FileVisitResult visitFile(
        Path file,
        BasicFileAttributes attrs
) throws IOException {

    System.out.println(file);
    return FileVisitResult.CONTINUE;
}
```

这是最常用的回调。

可以做：

```text
搜索文件
统计数量
统计文件大小
筛选扩展名
删除文件
读取文件信息
```

例如统计 `.java`：

```java
if (file.toString().endsWith(".java")) {
    System.out.println(file);
}
```

---

# 40. visitFileFailed()：访问失败怎么办？

```java
@Override
public FileVisitResult visitFileFailed(
        Path file,
        IOException exc
) throws IOException {

    System.err.println("无法访问：" + file);
    System.err.println("原因：" + exc.getMessage());

    return FileVisitResult.CONTINUE;
}
```

例如：

```text
没有权限
文件被删除
磁盘异常
链接目标异常
```

都可能导致访问失败。

你返回：

```java
CONTINUE
```

表示：

> 这个失败就算了，继续遍历别的。

如果你选择重新抛异常：

```java
throw exc;
```

则整个遍历可能直接结束。

这是实际开发里一个很重要的设计问题：

> 一个文件失败，是“忽略并继续”，还是“整个任务失败”？

要根据业务决定。

---

# 41. postVisitDirectory()：目录遍历结束

```java
@Override
public FileVisitResult postVisitDirectory(
        Path dir,
        IOException exc
) throws IOException {
    return FileVisitResult.CONTINUE;
}
```

它发生在：

> 目录里面所有东西都处理完以后。

这个时机非常适合：

```text
删除空目录
DFS 回溯阶段统计
离开目录时输出日志
```

例如递归删除目录时：

```text
必须先删里面的文件
↓
再删最里面目录
↓
再一路往外删
```

所以 `postVisitDirectory()` 非常适合删除目录本身。

---

# 42. FileVisitResult：控制 DFS 下一步怎么走

它是枚举：

```java
FileVisitResult.CONTINUE
FileVisitResult.TERMINATE
FileVisitResult.SKIP_SUBTREE
FileVisitResult.SKIP_SIBLINGS
```

---

## 42.1 CONTINUE

```java
return FileVisitResult.CONTINUE;
```

意思：

> 继续正常遍历。

绝大多数代码都是这个。

---

## 42.2 TERMINATE

```java
return FileVisitResult.TERMINATE;
```

意思：

> 整个遍历立即结束。

搜索第一个匹配文件时非常有用。

---

## 42.3 SKIP_SUBTREE

一般在：

```java
preVisitDirectory()
```

里面使用。

意思：

> 这个目录我知道了，但它内部不用遍历。

例如跳过：

```text
.git
node_modules
target
out
```

---

## 42.4 SKIP_SIBLINGS

意思大致是：

> 当前节点处理以后，同一级后面的兄弟节点不再访问。

这个用得少得多。

现阶段知道存在即可。

---

# 43. 实战 1：打印所有文件

```java
Path start = Path.of("D:\\test");

Files.walkFileTree(
        start,
        new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(
                    Path file,
                    BasicFileAttributes attrs
            ) throws IOException {

                System.out.println(file);
                return FileVisitResult.CONTINUE;
            }
        }
);
```

核心思维：

```text
每遇到文件
↓
visitFile()
↓
打印
↓
继续
```

---

# 44. 实战 2：搜索指定文件

```java
String targetName = "Main.java";

Files.walkFileTree(
        Path.of("D:\\Code"),
        new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(
                    Path file,
                    BasicFileAttributes attrs
            ) throws IOException {

                String fileName = file.getFileName().toString();

                if (fileName.equals(targetName)) {
                    System.out.println("找到：" + file);
                    return FileVisitResult.TERMINATE;
                }

                return FileVisitResult.CONTINUE;
            }
        }
);
```

注意：

```java
file.getFileName()
```

返回 `Path`。

所以比较字符串时：

```java
file.getFileName().toString()
```

---

# 45. 实战 3：统计所有文件总大小

因为匿名内部类里不能随便修改普通局部变量：

```java
long total = 0;
```

所以可以使用：

```java
AtomicLong total = new AtomicLong();
```

示例：

```java
AtomicLong total = new AtomicLong();

Files.walkFileTree(
        Path.of("D:\\test"),
        new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(
                    Path file,
                    BasicFileAttributes attrs
            ) throws IOException {

                total.addAndGet(attrs.size());
                return FileVisitResult.CONTINUE;
            }
        }
);

System.out.println("总大小：" + total.get() + " bytes");
```

这里正好使用：

```java
attrs.size()
```

避免再单独：

```java
Files.size(file)
```

因为遍历时 Java 已经把属性给你了。

---

# 46. 实战 4：递归删除整个目录

这是 `walkFileTree()` 最经典的例子之一。

假设：

```text
temp
├── a.txt
└── child
    └── b.txt
```

不能直接：

```java
Files.delete(Path.of("temp"));
```

因为目录里面还有东西。

正确思路：

```text
先删文件
↓
子目录空了以后删子目录
↓
最后删根目录
```

代码：

```java
Path root = Path.of("temp");

Files.walkFileTree(
        root,
        new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(
                    Path file,
                    BasicFileAttributes attrs
            ) throws IOException {

                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(
                    Path dir,
                    IOException exc
            ) throws IOException {

                if (exc != null) {
                    throw exc;
                }

                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        }
);
```

这段代码特别值得理解。

为什么目录删除写在：

```java
postVisitDirectory()
```

而不是：

```java
preVisitDirectory()
```

因为：

```text
preVisitDirectory
目录还没进去
里面还有文件
不能删

postVisitDirectory
里面已经处理完
目录变空
现在可以删
```

这就是 DFS 回溯思想在文件系统里的真实应用。

---

# 47. Files.list()、walk()、walkFileTree() 到底怎么选？

## 只看当前目录一层

用：

```java
Files.list()
```

## 递归获取所有路径，然后用 Stream 筛选

用：

```java
Files.walk()
```

## 需要精细控制 DFS 过程

例如：

```text
进入目录前做事
离开目录后做事
跳过某些目录
处理访问失败
找到以后立即终止
递归删除目录
```

用：

```java
Files.walkFileTree()
```

可以记成：

```text
list
一层

walk
递归 + Stream

walkFileTree
递归 + 完整控制
```

---

# 48. IOException：为什么这些方法总让我处理异常？

大量 Files 方法都会：

```java
throws IOException
```

例如：

```java
Files.createFile(path);
Files.copy(source, target);
Files.readString(path);
Files.walkFileTree(...);
```

原因很简单：

> 文件系统属于 Java 程序外部环境，任何操作都有可能失败。

例如：

```text
文件不存在
没有权限
磁盘满了
目录不存在
文件被其他程序占用
硬盘发生错误
网络磁盘断开
```

所以 Java 强迫你考虑失败情况。

学习/demo 可以：

```java
public static void main(String[] args) throws IOException {
    ...
}
```

实际业务代码一般会在合适的层级捕获和处理，而不是一路无脑 `throws`。

---

# 49. 一份适合当前阶段的完整 import

如果你在练习这节，可以经常用到：

```java
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
```

注意：

```java
import java.nio.file.*;
```

能导入：

```text
Path
Paths
Files
SimpleFileVisitor
FileVisitResult
StandardCopyOption
StandardOpenOption
```

但是不会自动导入子包：

```java
java.nio.file.attribute.BasicFileAttributes
```

因为 Java 的：

```java
import xxx.*;
```

**不会递归导入子包。**

---

# 50. 初学阶段最容易犯的错误

## 错误 1：以为 Path.of() 会创建文件

错误理解：

```java
Path.of("abc.txt");
```

文件就出来了。

实际：不会。

真正创建：

```java
Files.createFile(path);
```

---

## 错误 2：以为 `.txt` 一定代表文件

```java
Path.of("abc.txt")
```

`Path` 不会因此断定它是普通文件。

到底是什么，要看真实文件系统。

---

## 错误 3：把相对路径理解成相对于 Java 源文件

错。

通常相对于：

```java
System.getProperty("user.dir")
```

---

## 错误 4：createFile() 能自动建父目录

不能。

```java
Path file = Path.of("a", "b", "c.txt");

Files.createDirectories(file.getParent());
Files.createFile(file);
```

---

## 错误 5：readAllLines() 什么文件都用

小文件很舒服。

巨大日志文件会占大量内存。

大文件通常考虑：

```java
BufferedReader
Files.lines()
InputStream
FileChannel
```

---

## 错误 6：Files.list()/walk() 返回 Stream 后忘记关闭

推荐：

```java
try (Stream<Path> stream = Files.walk(path)) {
    ...
}
```

---

## 错误 7：递归删除时先删目录

非空目录删不了。

正确：

```text
visitFile
先删文件

postVisitDirectory
再删目录
```

---

# 51. 从专业开发角度，哪些方法最值得现在掌握？

不要平均用力。

## 第一梯队：必须熟练

```java
Path.of(...)
path.getFileName()
path.getParent()
path.resolve(...)
path.toAbsolutePath()

Files.exists(...)
Files.isRegularFile(...)
Files.isDirectory(...)
Files.createDirectories(...)
Files.copy(...)
Files.move(...)
Files.deleteIfExists(...)
Files.readString(...)
Files.writeString(...)
```

这些在普通 Java 项目和后端开发里非常实用。

---

## 第二梯队：理解并能查 API 使用

```java
path.normalize()
path.relativize(...)
path.resolveSibling(...)

Files.readAllLines(...)
Files.newBufferedReader(...)
Files.newBufferedWriter(...)
Files.size(...)
Files.readAttributes(...)
Files.list(...)
Files.walk(...)
```

---

## 第三梯队：重点理解机制，不必背模板

```java
Files.walkFileTree(...)
SimpleFileVisitor
FileVisitResult
BasicFileAttributes
```

你只要明白：

```text
walkFileTree = DFS 框架
```

以后需要时查一下方法签名，很快就能写出来。

这才是正常程序员的工作方式。

> **程序员不是 API 背诵机器。真正重要的是知道某个能力存在、理解它的模型，并能快速查到正确用法。**

---

# 52. 一张总图串起来

```text
                       Java NIO 文件 API
                              │
          ┌───────────────────┼───────────────────┐
          │                   │                   │
        Path                Files              各种 Option
        路径                 操作                  选项
          │                   │                   │
 Path.of()              exists()          StandardCopyOption
 getFileName()          isDirectory()     StandardOpenOption
 getParent()            createFile()      LinkOption
 resolve()              createDirectories()
 normalize()            copy()
 relativize()           move()
 toAbsolutePath()       delete()
                        readString()
                        writeString()
                        list()
                        walk()
                        walkFileTree()
                             │
                             ↓
                    SimpleFileVisitor
                             │
              ┌──────────────┼──────────────┐
              │              │              │
   preVisitDirectory     visitFile   postVisitDirectory
                             │
                       attrs 参数
                             │
                  BasicFileAttributes
```

---

# 53. 最终记忆版

复习时只看这里：

```text
Path = 地址
Files = 根据地址干活

Path.of = 创建路径对象，不创建文件
getFileName = 最后一个路径名
getParent = 父路径
resolve = 路径拼接
resolveSibling = 换成同级路径
normalize = 整理 . 和 ..
relativize = 从 A 到 B 怎么走
toAbsolutePath = 转绝对路径

Files.exists = 是否存在
isRegularFile = 是否普通文件
isDirectory = 是否目录
createFile = 创建空文件
createDirectory = 创建一层目录
createDirectories = 父目录一起创建
copy = 复制
move = 移动/重命名
delete = 删除，不存在会报错
deleteIfExists = 存在就删
size = 文件字节数
readString = 整个文本读成 String
readAllLines = 整个文本读成 List<String>
writeString = 写一个 String
newBufferedReader = 流式逐行读
newBufferedWriter = 流式写

StandardCopyOption
= copy/move 怎么处理

StandardOpenOption
= 文件怎么打开

BasicFileAttributes
= 当前文件的大小、时间、类型等信息

Files.list
= 当前目录一层

Files.walk
= 递归 + Stream

Files.walkFileTree
= 文件系统版 DFS

preVisitDirectory
= 进入目录之前

visitFile
= 遇到文件

visitFileFailed
= 访问失败

postVisitDirectory
= 目录处理完、准备离开

CONTINUE
= 继续

TERMINATE
= 全部结束

SKIP_SUBTREE
= 这个目录不进去
```

---

# 54. 学习建议：你现在应该怎么练？

不要继续背 API。

建议自己写下面 6 个小程序：

1. 创建 `study/nio/test.txt`，父目录不存在时自动创建。
2. 往 `test.txt` 写三行文字，再逐行读取。
3. 把 `test.txt` 复制成 `test-copy.txt`，已存在时覆盖。
4. 遍历某个目录，打印其中所有 `.java` 文件。
5. 搜索第一个名为 `Main.java` 的文件，找到以后停止遍历。
6. 写一个 `deleteDirectory(Path root)`，递归删除整个非空目录。

如果这 6 个可以不照教程独立写出来，这一节就已经真正掌握了。

---

# 55. 最后一句：怎样才算“会用 API”？

不是把：

```java
Files.walkFileTree(Path, FileVisitor)
```

一字不差背出来。

真正的“会”是：

```text
遇到需求：我要递归找文件
↓
知道 Files 有 walk / walkFileTree
↓
知道 walkFileTree 类似 DFS
↓
知道主要逻辑写在 visitFile
↓
需要跳目录就在 preVisitDirectory
↓
忘记准确签名时查 IDE 补全 / Javadoc
↓
能正确写出来并处理异常
```

这才是以后做 Java 后端时真正需要的能力。
