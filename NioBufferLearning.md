# Java NIO Buffer 学习笔记

> 适用阶段：刚开始学习 Java NIO，已经接触过传统 IO，希望真正理解 `Buffer` 的工作机制。  
> 核心目标：掌握 `capacity`、`limit`、`position`、`mark`，以及 `put()`、`get()`、`flip()`、`clear()`、`rewind()`、`compact()` 对它们的影响。

---

## 1. Buffer 是什么？

在 Java NIO 中，`Buffer` 可以理解为：

> **一块用于临时存放数据的内存区域 + 一组用于描述“当前该操作哪里”的状态变量。**

最常见的是：

```java
ByteBuffer buffer = ByteBuffer.allocate(8);
```

这表示创建一个容量为 8 字节的缓冲区。

可以先把它想象成一个数组：

```text
下标       0   1   2   3   4   5   6   7
        +---+---+---+---+---+---+---+---+
Buffer  |   |   |   |   |   |   |   |   |
        +---+---+---+---+---+---+---+---+
```

但 `Buffer` 并不只是数组。

它还维护几个非常重要的状态：

```text
capacity
limit
position
mark
```

其中最核心的是：

```text
position
limit
capacity
```

---

# 2. Buffer 的四个核心属性

## 2.1 capacity：总容量

`capacity` 表示：

> Buffer 一共最多能够容纳多少个元素。

例如：

```java
ByteBuffer buffer = ByteBuffer.allocate(8);
```

那么：

```text
capacity = 8
```

合法下标是：

```text
0 ~ 7
```

### 特点

`capacity` 在 Buffer 创建之后通常不会改变。

可以把它理解为：

> **这块内存区域总共多大。**

---

## 2.2 position：下一次操作的位置

`position` 表示：

> 下一次相对 `get()` 或 `put()` 操作要访问的位置。

例如：

```java
ByteBuffer buffer = ByteBuffer.allocate(8);

buffer.put((byte) 'A');
```

最开始：

```text
position = 0
```

所以 `A` 会写入下标 0。

写完之后：

```text
position = 1
```

图示：

```text
        0   1   2   3   4   5   6   7
      +---+---+---+---+---+---+---+---+
      | A |   |   |   |   |   |   |   |
      +---+---+---+---+---+---+---+---+
            ↑
         position
```

继续：

```java
buffer.put((byte) 'B');
buffer.put((byte) 'C');
```

得到：

```text
        0   1   2   3   4   5   6   7
      +---+---+---+---+---+---+---+---+
      | A | B | C |   |   |   |   |   |
      +---+---+---+---+---+---+---+---+
                    ↑
                 position
```

此时：

```text
position = 3
```

注意：

> `position = 3` 不是说“当前正在操作下标 3”。

而是说：

> **0、1、2 已经处理完，下一次从下标 3 开始。**

---

## 2.3 limit：当前允许操作到哪里

`limit` 最容易被误解。

它不是简单表示：

> “Buffer 中有多少数据。”

更准确的定义是：

> **当前这一轮操作能够到达的边界。**

有效操作区间通常是：

```text
[position, limit)
```

也就是：

```text
position <= 可操作位置 < limit
```

`limit` 本身不能被相对 `get()` / `put()` 操作访问。

---

### 为什么刚创建 Buffer 时 limit = capacity？

例如：

```java
ByteBuffer buffer = ByteBuffer.allocate(8);
```

初始状态：

```text
position = 0
limit    = 8
capacity = 8
```

因为刚创建出来时，通常准备向 Buffer 中写数据。

这意味着：

```text
[0, 8)
```

整块 Buffer 都可以写。

图示：

```text
        当前可写区域
      <------------------------------->
        0   1   2   3   4   5   6   7
      +---+---+---+---+---+---+---+---+
      |   |   |   |   |   |   |   |   |
      +---+---+---+---+---+---+---+---+
        ↑                               ↑
     position                         limit

capacity = 8
```

因此，可以把 `limit` 理解为：

> **本轮操作的右边界。**

---

## 2.4 mark：给 position 做一个书签

`mark` 用来：

> 保存某一时刻的 `position`。

必须主动调用：

```java
buffer.mark();
```

它不会自动记录“上一次操作的位置”。

例如：

```text
position = 2
```

调用：

```java
buffer.mark();
```

相当于：

```text
mark = 2
```

之后继续读取：

```java
buffer.get();
buffer.get();
```

假设：

```text
position = 4
mark     = 2
```

再调用：

```java
buffer.reset();
```

那么：

```text
position = mark = 2
```

因此可以这样记：

```text
mark()  ：保存当前 position
reset() ：回到 mark
```

类比：

```text
mark()  ≈ 存档
reset() ≈ 读档
```

---

# 3. Buffer 的状态关系

Buffer 的几个核心变量始终满足：

```text
0 <= mark <= position <= limit <= capacity
```

如果 `mark` 尚未设置，则忽略它即可。

可以理解成：

```text
0        mark        position        limit        capacity
|----------|-------------|-------------|-------------|
```

---

# 4. Buffer 最重要的概念：写模式和读模式

Java 的 Buffer 内部没有一个真正叫：

```java
readMode
writeMode
```

的属性。

但开发中通常会说：

- 写模式
- 读模式

所谓“模式”，本质上只是：

> **position 和 limit 被设置成了适合当前操作的状态。**

---

# 5. 写入数据时，position 如何变化？

创建：

```java
ByteBuffer buffer = ByteBuffer.allocate(8);
```

初始：

```text
position = 0
limit    = 8
capacity = 8
```

写入：

```java
buffer.put((byte) 'A');
buffer.put((byte) 'B');
buffer.put((byte) 'C');
```

结果：

```text
        已写数据             剩余可写空间
      <----------> <-------------------->
        0   1   2   3   4   5   6   7
      +---+---+---+---+---+---+---+---+
      | A | B | C |   |   |   |   |   |
      +---+---+---+---+---+---+---+---+
                    ↑                   ↑
                 position             limit

position = 3
limit    = 8
capacity = 8
```

相对 `put()` 的行为，可以近似理解为：

```java
buffer[position] = value;
position++;
```

---

# 6. 为什么写完以后不能直接 get()？

此时：

```text
position = 3
```

如果直接：

```java
buffer.get();
```

Buffer 会从下标 3 开始读取。

但真正的数据：

```text
A B C
```

在：

```text
0 1 2
```

所以需要把：

```text
position
```

移回 0，

同时告诉 Buffer：

> 只允许读刚才真正写进去的 3 个字节。

这就是：

```java
buffer.flip();
```

---

# 7. flip()：从写数据切换到读数据

`flip()` 的核心行为是：

```text
limit = 原来的 position
position = 0
mark 被清除
```

例如 flip 前：

```text
position = 3
limit    = 8
capacity = 8
```

执行：

```java
buffer.flip();
```

之后：

```text
position = 0
limit    = 3
capacity = 8
```

图示：

```text
        可以读取的数据           本轮不可访问
      <-----------------> <-------------------->
        0   1   2   3   4   5   6   7
      +---+---+---+---+---+---+---+---+
      | A | B | C | ? | ? | ? | ? | ? |
      +---+---+---+---+---+---+---+---+
        ↑           ↑                   ↑
     position      limit              capacity
```

现在有效读取区间是：

```text
[position, limit)

= [0, 3)

= 0、1、2
```

刚好是：

```text
A B C
```

---

## 7.1 flip() 不会翻转数据

特别注意：

```java
buffer.flip();
```

不会把：

```text
ABC
```

变成：

```text
CBA
```

它只修改：

```text
position
limit
mark
```

Buffer 中的数据本身并不会因此改变。

---

# 8. get() 如何改变 position？

flip 后：

```text
position = 0
limit    = 3
```

执行：

```java
buffer.get();
```

读取：

```text
A
```

然后：

```text
position = 1
```

再执行：

```java
buffer.get();
```

读取：

```text
B
```

然后：

```text
position = 2
```

再执行：

```java
buffer.get();
```

读取：

```text
C
```

最后：

```text
position = 3
limit    = 3
```

此时：

```text
position == limit
```

说明：

> 当前已经没有可以继续读取的数据。

继续调用相对 `get()`，会产生：

```text
BufferUnderflowException
```

---

# 9. remaining() 和 hasRemaining()

## remaining()

```java
buffer.remaining();
```

表示：

> 当前还剩多少个元素可以操作。

本质：

```text
remaining = limit - position
```

例如：

```text
position = 1
limit    = 3
```

那么：

```text
remaining = 2
```

---

## hasRemaining()

```java
buffer.hasRemaining();
```

表示：

> 是否还有剩余元素可以操作。

本质相当于：

```java
position < limit
```

因此经常看到：

```java
while (buffer.hasRemaining()) {
    byte b = buffer.get();
}
```

---

# 10. 相对 get/put 和绝对 get/put

Buffer 有两类操作。

---

## 10.1 相对操作

例如：

```java
buffer.get();
buffer.put(value);
```

它们会使用当前：

```text
position
```

然后自动：

```text
position++
```

---

## 10.2 绝对操作

例如：

```java
buffer.get(2);
buffer.put(2, value);
```

这里显式指定了下标。

例如：

```java
byte value = buffer.get(2);
```

表示：

> 直接访问下标 2。

这种操作：

> **不会修改 position。**

---

# 11. clear()：读完以后准备重新写

假设 ABC 已经读取完成：

```text
position = 3
limit    = 3
capacity = 8
```

执行：

```java
buffer.clear();
```

之后：

```text
position = 0
limit    = capacity
mark     = 清除
```

也就是：

```text
position = 0
limit    = 8
capacity = 8
```

---

## 11.1 clear() 不会真正删除数据

执行 clear 后，内存中的数据可能仍然是：

```text
A B C ...
```

例如：

```text
        0   1   2   3   4   5   6   7
      +---+---+---+---+---+---+---+---+
      | A | B | C | ? | ? | ? | ? | ? |
      +---+---+---+---+---+---+---+---+
        ↑                               ↑
     position                         limit
```

但 Buffer 的逻辑状态变成：

> 这整块区域都可以重新写入。

下一次：

```java
buffer.put((byte) 'X');
```

会覆盖原来的 `A`。

因此：

> `clear()` 是逻辑清空，不是物理清零。

---

# 12. flip() 和 clear() 是一对常用操作

可以理解为：

```text
        写数据
           ↓
     +------------+
     |   写模式   |
     +------------+
           |
         flip()
           ↓
     +------------+
     |   读模式   |
     +------------+
           |
        clear()
           ↓
     +------------+
     |   写模式   |
     +------------+
```

典型代码：

```java
ByteBuffer buffer = ByteBuffer.allocate(1024);

channel.read(buffer);

buffer.flip();

// 读取 / 处理 Buffer 中的数据

buffer.clear();
```

---

# 13. 一个非常容易混淆的地方：channel.read(buffer)

例如：

```java
channel.read(buffer);
```

看起来名字叫：

```text
read
```

但实际上数据却被写进了 Buffer。

这是因为：

> `read()` 是站在 Channel 的角度命名的。

意思是：

```text
从 Channel 中读取数据
        ↓
写进 Buffer
```

数据流向：

```text
Channel -------> Buffer
```

因此：

```java
channel.read(buffer);
```

会推动 Buffer 的：

```text
position
```

向右移动。

---

# 14. rewind()：重新从头读取

假设当前 Buffer 中有：

```text
A B C
```

并且：

```text
position = 2
limit    = 3
```

表示：

```text
A B 已经读过
C 还没读
```

如果想：

> 不改变 limit，再从头重新读一遍。

可以：

```java
buffer.rewind();
```

它会：

```text
position = 0
limit    = 不变
mark     = 清除
```

于是又可以从 A 开始读取。

---

## flip() 和 rewind() 的区别

### flip()

```text
limit = 原 position
position = 0
```

用途：

> 写完数据，准备开始读。

---

### rewind()

```text
limit 不变
position = 0
```

用途：

> 已经处于读状态，但想重新从头读。

---

# 15. compact()：保留没处理完的数据，再继续写

`compact()` 在网络编程中特别重要。

假设 Buffer 中有：

```text
A B C D E
```

当前：

```text
position = 2
limit    = 5
```

也就是说：

```text
A B       已经处理
C D E     还没处理
```

图示：

```text
          已处理        未处理
        <-------> <------------>
          A   B   C   D   E
        +---+---+---+---+---+---+---+---+
        | A | B | C | D | E |   |   |   |
        +---+---+---+---+---+---+---+---+
                  ↑           ↑
               position     limit
```

这时如果直接：

```java
buffer.clear();
```

那么 `C D E` 也会被当成废弃数据。

如果它们还不能丢，就应该使用：

```java
buffer.compact();
```

---

## compact() 会做什么？

它会把：

```text
[position, limit)
```

也就是：

```text
C D E
```

移动到 Buffer 最前面：

```text
C D E _ _ _ _ _
```

然后：

```text
position = 3
limit    = capacity
```

图示：

```text
        保留下来的数据          可以继续写
      <---------------> <------------------->
        0   1   2   3   4   5   6   7
      +---+---+---+---+---+---+---+---+
      | C | D | E |   |   |   |   |   |
      +---+---+---+---+---+---+---+---+
                    ↑                   ↑
                 position             limit
```

之后新数据可以继续追加到 E 后面。

---

# 16. compact() 为什么适合 TCP / HTTP？

TCP 是字节流协议。

一次：

```java
channel.read(buffer);
```

并不保证正好读取一条完整 HTTP 请求。

例如服务端第一次收到：

```text
GET /hello HTTP/1.1\r\n
Host: lo
```

下一次才收到：

```text
calhost\r\n
\r\n
```

如果第一次解析后：

```text
GET /hello HTTP/1.1
```

已经处理完成，

但：

```text
Host: lo
```

还不完整，

这时不能：

```java
buffer.clear();
```

因为它会把未处理数据也视为可覆盖区域。

应该：

```java
buffer.compact();
```

把：

```text
Host: lo
```

保留下来，

然后继续读取下一批 TCP 数据：

```text
calhost\r\n\r\n
```

最终拼成：

```text
Host: localhost\r\n\r\n
```

这就是 `compact()` 在网络协议解析中的典型用途。

---

# 17. 核心方法对状态的影响

| 操作 | position | limit | 数据本身 | 常见用途 |
|---|---:|---:|---|---|
| `put()` | 增加 | 不变 | 写入 / 覆盖 | 向 Buffer 放数据 |
| `get()` | 增加 | 不变 | 不删除 | 从 Buffer 取数据 |
| `flip()` | 变为 0 | 变为原 position | 不变 | 写完 → 读 |
| `clear()` | 变为 0 | 变为 capacity | 不真正删除 | 读完 → 全部重新写 |
| `rewind()` | 变为 0 | 不变 | 不变 | 从头重新读 |
| `compact()` | 变为剩余数据长度 | 变为 capacity | 剩余数据被移动 | 保留未读数据 → 继续写 |
| `mark()` | 不变 | 不变 | 不变 | 保存 position |
| `reset()` | 变为 mark | 不变 | 不变 | 回到 mark |

---

# 18. 一套完整状态变化

创建：

```java
ByteBuffer buffer = ByteBuffer.allocate(8);
```

---

## 第 1 步：初始状态

```text
数据       [ _ _ _ _ _ _ _ _ ]

position = 0
limit    = 8
capacity = 8
```

---

## 第 2 步：put('A')

```text
数据       [ A _ _ _ _ _ _ _ ]
               ↑

position = 1
limit    = 8
capacity = 8
```

---

## 第 3 步：put('B')、put('C')

```text
数据       [ A B C _ _ _ _ _ ]
                   ↑

position = 3
limit    = 8
capacity = 8
```

---

## 第 4 步：flip()

```text
数据       [ A B C | _ _ _ _ _ ]
             ↑     ↑
            pos   limit

position = 0
limit    = 3
capacity = 8
```

---

## 第 5 步：get()

读取 A：

```text
position = 1
limit    = 3
```

---

## 第 6 步：get()

读取 B：

```text
position = 2
limit    = 3
```

---

## 第 7 步：get()

读取 C：

```text
position = 3
limit    = 3
```

此时：

```text
remaining = 0
```

---

## 第 8 步：clear()

```text
position = 0
limit    = 8
capacity = 8
```

Buffer 又可以用于下一轮写入。

---

# 19. FileChannel 中最典型的 Buffer 使用模式

常见代码：

```java
ByteBuffer buffer = ByteBuffer.allocate(1024);

while (sourceChannel.read(buffer) != -1) {

    buffer.flip();

    destinationChannel.write(buffer);

    buffer.clear();
}
```

理解：

### ① `sourceChannel.read(buffer)`

```text
sourceChannel
       ↓
     Buffer
```

从 Channel 读取数据，写入 Buffer。

此时：

```text
position 向右移动
```

---

### ② `buffer.flip()`

把：

```text
刚才写入的区域
```

转换为：

```text
现在允许读取的区域
```

---

### ③ `destinationChannel.write(buffer)`

从 Buffer 中读取数据，写入目标 Channel。

此时 Buffer 的：

```text
position
```

继续向右移动。

---

### ④ `buffer.clear()`

这一批数据已经使用完。

恢复为：

```text
position = 0
limit    = capacity
```

准备下一轮读取。

---

# 20. Buffer 的核心图

## 写状态

```text
已经写过的数据              还能继续写
<---------------><--------------------------->
0            position                       limit
                                              =
                                           capacity
```

此时通常：

```text
limit = capacity
```

---

## 读状态

```text
已经读过              还能读              不参与本轮读取
<-----------><-------------------><------------------->
0        position               limit             capacity
```

此时：

```text
[position, limit)
```

就是：

> 当前还能读取的数据。

---

# 21. 最推荐的理解方式

不要只背下面这种定义：

```text
capacity：容量
limit：限制
position：位置
mark：标记
```

这样很容易考试会背、代码不会写。

更推荐这样记：

```text
capacity
= 这块地一共有多大

limit
= 这一轮最多允许操作到哪里

position
= 下一次从哪里开始操作

mark
= 我在这个 position 插了一个书签
```

---

# 22. 方法口诀

```text
flip
写完了，回头读

clear
这批读完了，整块重新写

rewind
数据还不变，我重新从头读

compact
没处理完的数据先留着，再继续写

mark
在当前位置做书签

reset
回到书签
```

---

# 23. 两个必须记住的公式

Buffer 的核心状态关系：

```text
0 <= mark <= position <= limit <= capacity
```

剩余可操作元素：

```java
buffer.remaining()
```

本质：

```text
remaining = limit - position
```

判断是否还有数据：

```java
buffer.hasRemaining()
```

本质：

```text
position < limit
```

---

# 24. 常见误区

## 误区 1：limit 就是当前数据量

不完全正确。

刚创建：

```java
ByteBuffer.allocate(8);
```

时：

```text
limit = 8
```

但此时根本没有写入 8 个数据。

更准确：

> `limit` 是当前操作的边界。

---

## 误区 2：flip() 会翻转数据

错误。

它不会：

```text
ABC -> CBA
```

它只是改变：

```text
position
limit
mark
```

---

## 误区 3：clear() 会把 Buffer 清零

错误。

它只是重新设置：

```text
position = 0
limit = capacity
```

旧数据通常还留在底层内存中，只是允许后续数据覆盖。

---

## 误区 4：mark 会自动记录位置

错误。

只有显式执行：

```java
buffer.mark();
```

才会记录当前 position。

---

## 误区 5：channel.read(buffer) 是从 Buffer 里读取

错误。

`read` 是站在 Channel 的角度。

```text
Channel -> Buffer
```

即：

> 从 Channel 读取，写入 Buffer。

---

## 误区 6：一次 SocketChannel.read() 就是一条完整消息

错误。

TCP 只保证：

> 有序字节流。

不保证：

```text
一次 read == 一个 HTTP 请求
```

因此实际网络协议解析中，经常需要：

```text
flip()
解析
compact()
继续 read()
```

---

# 25. 最终总结

如果只保留 Buffer 最重要的知识，可以浓缩成下面几句话：

> `capacity` 是 Buffer 的总容量。  
> `position` 是下一次操作的位置。  
> `limit` 是本轮操作的右边界。  
> `mark` 是人为保存的 position。

Buffer 的核心操作过程：

```text
Channel / 程序
      ↓
    写入
      ↓
   Buffer
      ↓
    flip()
      ↓
    读取
      ↓
clear() / compact()
      ↓
下一轮写入
```

最典型的状态切换：

```text
写数据
  ↓
flip()
  ↓
读数据
  ↓
clear()
  ↓
重新写
```

如果还有未处理完的数据：

```text
读了一部分
    ↓
compact()
    ↓
保留剩余数据
    ↓
继续写入新数据
```

真正掌握 Buffer 的标志，不是能背 API，而是看到：

```text
position
limit
capacity
```

时，能够立即判断：

1. 哪些数据已经处理；
2. 哪些数据还能处理；
3. 下一次操作从哪里开始；
4. 当前 Buffer 更适合继续读还是继续写；
5. 现在应该调用 `flip()`、`clear()` 还是 `compact()`。

---

## 一句话记忆

```text
capacity：总共多大
limit：这轮最多到哪
position：下一次从哪开始
mark：给 position 做书签
```

以及：

```text
flip     ：写完 → 读
clear    ：读完 → 全部重新写
rewind   ：重新从头读
compact  ：保留没读完的 → 继续写
```
