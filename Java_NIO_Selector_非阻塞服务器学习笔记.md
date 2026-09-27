# Java NIO：使用 Selector 编写非阻塞服务器学习笔记

> 目标：不是死记 Demo，而是理解为什么这么写、每个类负责什么、每一步代码在干什么。

---

## 一、为什么需要 Selector？

传统阻塞式 Socket 中，`accept()`、`read()` 都可能阻塞线程。

例如：

```text
客户端 A 连接
    ↓
服务器线程调用 read(A)
    ↓
A 一直不发数据
    ↓
服务器线程被卡住
```

传统做法往往是：

```text
一个客户端 → 一个线程
```

连接少时没问题，但连接很多时，会产生线程栈、线程调度和上下文切换等开销。

NIO 的思路是：

> **让一个线程通过 Selector 同时管理多个 Channel，只处理真正已经就绪的连接。**

可以把 `Selector` 理解成“事件管理员”：

```text
Client A ── SocketChannel A ─┐
Client B ── SocketChannel B ─┼──→ Selector
Client C ── SocketChannel C ─┘
```

服务器线程调用：

```java
selector.select();
```

等待某个关注的事件发生。谁有事件，就处理谁。

---

## 二、ServerSocketChannel 和 SocketChannel

### 1. ServerSocketChannel

```java
ServerSocketChannel serverSocketChannel =
        ServerSocketChannel.open();
```

它主要负责：

```text
监听端口
接受新客户端连接
```

绑定端口：

```java
serverSocketChannel.bind(
        new InetSocketAddress(8888)
);
```

接受连接：

```java
SocketChannel socketChannel =
        serverSocketChannel.accept();
```

### 2. SocketChannel

`accept()` 返回的 `SocketChannel` 表示：

> **服务器和某一个具体客户端之间的一条 TCP 连接。**

它主要负责：

```java
read(...)
write(...)
```

可以记：

```text
ServerSocketChannel = 接客
SocketChannel       = 和某一个客人聊天
```

关系：

```text
             ServerSocketChannel
                /          \
           accept()      accept()
              ↓             ↓
      SocketChannel A SocketChannel B
              │             │
          Client A       Client B
```

---

## 三、为什么 configureBlocking(false)？

```java
serverSocketChannel.configureBlocking(false);
```

或者：

```java
socketChannel.configureBlocking(false);
```

作用：

> 把 Channel 设置为非阻塞模式。

Selector 要实现：

```text
A 没数据 → 不处理
B 有数据 → 处理 B
C 没数据 → 不处理
```

如果 Channel 还是阻塞模式：

```java
channel.read(buffer);
```

没有数据时线程可能直接卡住，那么一个线程管理多个连接就失去意义。

另外：

> **Channel 想注册到 Selector，必须先设置成非阻塞模式。**

所以顺序通常是：

```text
configureBlocking(false)
        ↓
register(...)
```

---

## 四、创建 Selector

```java
Selector selector = Selector.open();
```

可以理解成：

```text
创建一个事件管理员
```

它负责：

```text
监听多个 Channel
判断哪些事件已经就绪
把对应的 SelectionKey 交给程序处理
```

---

## 五、register() 是干什么的？

例如：

```java
serverSocketChannel.register(
        selector,
        SelectionKey.OP_ACCEPT
);
```

意思：

> 把这个 Channel 注册给 Selector，并告诉 Selector：我关心 ACCEPT 事件。

再比如：

```java
socketChannel.register(
        selector,
        SelectionKey.OP_READ
);
```

意思：

> 这个客户端以后有数据可以读时，请通知我。

所以：

```java
channel.register(selector, 某种事件);
```

可以理解为：

> **登记 Channel + 订阅事件。**

---

## 六、OP_ACCEPT、OP_READ 等事件

常见事件：

```text
OP_ACCEPT
有新客户端可以接受

OP_CONNECT
客户端连接过程完成

OP_READ
当前 Channel 有数据可以读取

OP_WRITE
当前 Channel 可以继续写数据
```

目前最重要的是：

```text
OP_ACCEPT
OP_READ
```

### OP_ACCEPT

注册：

```java
serverSocketChannel.register(
        selector,
        SelectionKey.OP_ACCEPT
);
```

之后：

```java
if (key.isAcceptable()) {
    ...
}
```

表示：

> 当前是不是发生了“新客户端可以接受”的事件？

如果是：

```java
SocketChannel socketChannel =
        serverSocketChannel.accept();
```

### OP_READ

新客户端连接后：

```java
socketChannel.register(
        selector,
        SelectionKey.OP_READ
);
```

之后：

```java
if (key.isReadable()) {
    ...
}
```

表示：

> 当前这个客户端是不是有数据可以读？

---

## 七、SelectionKey 是什么？

注册时：

```java
SelectionKey key =
        socketChannel.register(
                selector,
                SelectionKey.OP_READ
        );
```

会得到一个 `SelectionKey`。

可以把它理解成：

> **Channel 在 Selector 那里的注册凭证。**

它把这些信息联系起来：

```text
哪个 Channel？
注册在哪个 Selector？
关注哪些事件？
当前哪些事件已经 ready？
```

例如：

```text
┌────────────────────────┐
│     SelectionKey       │
│                        │
│ Channel：客户端 A       │
│ Selector：selector      │
│ 关注：OP_READ           │
└────────────────────────┘
```

通过：

```java
key.channel();
```

可以拿回对应的 Channel。

常见写法：

```java
SocketChannel socketChannel =
        (SocketChannel) key.channel();
```

---

## 八、selector.select() 是干什么的？

```java
selector.select();
```

意思：

> **等待至少一个我关心的事件发生。**

假设当前：

```text
ServerSocketChannel → OP_ACCEPT

SocketChannel A → OP_READ

SocketChannel B → OP_READ
```

如果此时：

```text
有新客户端 C 连接
同时
客户端 B 发来数据
```

Selector 会发现：

```text
ServerSocketChannel：OP_ACCEPT ready
SocketChannel B：OP_READ ready
```

然后 `select()` 返回。

---

## 九、selectedKeys() 里是什么？

```java
Set<SelectionKey> selectedKeys =
        selector.selectedKeys();
```

里面装的是：

> **当前已经发生就绪事件、需要你处理的 SelectionKey。**

注意：

```text
不是所有注册过的 key
而是当前真正有事件的 key
```

例如：

```text
ServerSocketChannel → OP_ACCEPT ready
SocketChannel B     → OP_READ ready
```

那么 `selectedKeys()` 中大概就是这两个 Channel 对应的 key。

---

## 十、为什么使用 Iterator？

通常这样遍历：

```java
Iterator<SelectionKey> iterator =
        selector.selectedKeys().iterator();

while (iterator.hasNext()) {

    SelectionKey key =
            iterator.next();

    iterator.remove();

    ...
}
```

原因是：

> 我们需要一边遍历，一边把已经处理过的 key 从 `selectedKeys` 中删除。

---

## 十一、为什么 iterator.remove()？

```java
iterator.remove();
```

不是关闭连接，也不是注销 Channel。

它只是：

> **把当前 key 从这一轮待处理事件集合中移除。**

`selectedKeys()` 中的 key 不会因为你处理完就自动消失。

所以：

```text
事件处理完
    ↓
iterator.remove()
    ↓
从当前待处理集合移除
```

可以把它理解成“已处理，标记已读”。

---

## 十二、处理新客户端连接

典型代码：

```java
if (key.isAcceptable()) {

    SocketChannel socketChannel =
            serverSocketChannel.accept();

    socketChannel.configureBlocking(false);

    socketChannel.register(
            selector,
            SelectionKey.OP_READ
    );
}
```

这几步分别是：

```text
1. accept()
   接收新客户端

2. 得到 SocketChannel
   表示和这个客户端的 TCP 连接

3. configureBlocking(false)
   设置为非阻塞模式

4. register(... OP_READ)
   以后这个客户端有数据时通知我
```

完整流程：

```text
新客户端连接
    ↓
OP_ACCEPT
    ↓
accept()
    ↓
SocketChannel
    ↓
设置非阻塞
    ↓
注册 OP_READ
```

---

## 十三、处理客户端发来的数据

典型代码：

```java
if (key.isReadable()) {

    SocketChannel socketChannel =
            (SocketChannel) key.channel();

    ByteBuffer buffer =
            ByteBuffer.allocate(1024);

    int read =
            socketChannel.read(buffer);

    ...
}
```

其中：

```java
key.channel();
```

表示：

> 拿到当前这个 SelectionKey 对应的 Channel。

然后：

```java
socketChannel.read(buffer);
```

把网络数据读进 `ByteBuffer`。

---

## 十四、read() 的返回值

```java
int read = socketChannel.read(buffer);
```

常见三种情况：

```text
read > 0
本次真的读取到了数据

read == 0
当前暂时没有读到数据

read == -1
对方已经关闭连接
```

### read > 0

```java
if (read > 0) {

    buffer.flip();

    System.out.println(
            StandardCharsets.UTF_8.decode(buffer)
    );
}
```

### read == 0

非阻塞模式下很正常。

表示：

```text
当前这次没有真正读到数据
```

不是异常。

### read == -1

表示：

> **对方已经关闭连接，数据流到达末尾。**

通常：

```java
key.cancel();
socketChannel.close();
```

---

## 十五、key.cancel() 是什么？

前面说：

```text
SelectionKey
=
Channel 在 Selector 上的注册凭证
```

所以：

```java
key.cancel();
```

就是：

> **取消这个 Channel 在 Selector 上的注册。**

和：

```java
socketChannel.close();
```

不是同一个概念。

前者：

```text
取消 Selector 注册
```

后者：

```text
真正关闭网络连接
```

客户端断开后一般两个都做：

```java
key.cancel();
socketChannel.close();
```

---

## 十六、为什么 read() 后要 buffer.flip()？

创建：

```java
ByteBuffer buffer =
        ByteBuffer.allocate(1024);
```

刚开始：

```text
position = 0
limit = 1024
capacity = 1024
```

执行：

```java
socketChannel.read(buffer);
```

虽然方法叫 `read()`，但从 Buffer 的角度看，是：

```text
网络数据
    ↓
写进 Buffer
```

假设读了 20 字节：

```text
position = 20
limit = 1024
```

大概：

```text
0                  19                 1023
| 已写入的数据       |      空余空间       |
                    ↑
                 position
```

现在程序准备从 Buffer 里读取这些数据，所以要：

```java
buffer.flip();
```

`flip()` 大致会做：

```text
limit = 原 position
position = 0
```

变成：

```text
0                  19
|      有效数据      |
↑                   ↑
position           limit
```

所以可以记：

> **刚才往 Buffer 里写完了，现在要从 Buffer 里读，于是调用 flip()。**

---

## 十七、完整的基础非阻塞服务器

```java
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class NioServer {

    public static void main(String[] args)
            throws IOException {

        // 1. 创建 ServerSocketChannel
        ServerSocketChannel serverSocketChannel =
                ServerSocketChannel.open();

        // 2. 绑定端口
        serverSocketChannel.bind(
                new InetSocketAddress(8888)
        );

        // 3. 设置非阻塞
        serverSocketChannel.configureBlocking(false);

        // 4. 创建 Selector
        Selector selector =
                Selector.open();

        // 5. 监听新连接事件
        serverSocketChannel.register(
                selector,
                SelectionKey.OP_ACCEPT
        );

        while (true) {

            // 6. 等待至少一个事件就绪
            selector.select();

            // 7. 获取当前已经就绪的 SelectionKey
            Iterator<SelectionKey> iterator =
                    selector.selectedKeys()
                            .iterator();

            while (iterator.hasNext()) {

                SelectionKey key =
                        iterator.next();

                // 8. 当前事件从 selectedKeys 移除
                iterator.remove();

                // 9. 处理新连接
                if (key.isAcceptable()) {

                    SocketChannel socketChannel =
                            serverSocketChannel.accept();

                    socketChannel.configureBlocking(false);

                    socketChannel.register(
                            selector,
                            SelectionKey.OP_READ
                    );
                }

                // 10. 处理客户端数据
                if (key.isReadable()) {

                    SocketChannel socketChannel =
                            (SocketChannel) key.channel();

                    ByteBuffer buffer =
                            ByteBuffer.allocate(1024);

                    int read =
                            socketChannel.read(buffer);

                    if (read > 0) {

                        buffer.flip();

                        String message =
                                StandardCharsets.UTF_8
                                        .decode(buffer)
                                        .toString();

                        System.out.println(
                                "收到客户端消息：" + message
                        );

                    } else if (read == -1) {

                        System.out.println(
                                "客户端已断开"
                        );

                        key.cancel();
                        socketChannel.close();
                    }
                }
            }
        }
    }
}
```

---

## 十八、整个服务器运行流程

服务器启动：

```text
ServerSocketChannel.open()
    ↓
bind(8888)
    ↓
configureBlocking(false)
    ↓
Selector.open()
    ↓
注册 OP_ACCEPT
    ↓
selector.select()
    ↓
等待事件
```

客户端连接：

```text
Client A
    ↓
连接服务器
    ↓
Selector 发现 OP_ACCEPT
    ↓
key.isAcceptable()
    ↓
server.accept()
    ↓
得到 SocketChannel A
    ↓
设置非阻塞
    ↓
注册 OP_READ
```

客户端发数据：

```text
Client A
    ↓
发送数据
    ↓
Selector 发现 OP_READ
    ↓
key.isReadable()
    ↓
key.channel()
    ↓
拿到 SocketChannel A
    ↓
read(buffer)
    ↓
数据写进 ByteBuffer
    ↓
buffer.flip()
    ↓
读取 Buffer 中的数据
```

客户端断开：

```text
Client A close()
    ↓
服务器 read() == -1
    ↓
key.cancel()
    ↓
socketChannel.close()
```

---

## 十九、最重要的一张关系图

```text
                        Selector
                           │
             ┌─────────────┼─────────────┐
             │             │             │
        SelectionKey  SelectionKey  SelectionKey
             │             │             │
             │             │             │
 ServerSocketChannel SocketChannel A SocketChannel B
             │             │             │
         OP_ACCEPT       OP_READ       OP_READ
             │             │             │
          新连接          A发数据        B发数据
```

Selector 的工作就是：

```text
盯着所有注册进来的 Channel
        ↓
哪个事件 ready
        ↓
把对应 SelectionKey
放进 selectedKeys
        ↓
程序遍历 selectedKeys
        ↓
处理 ACCEPT / READ 等事件
```

---

## 二十、写服务器时可以按这 11 步回忆

```text
1. ServerSocketChannel.open()

2. bind(port)

3. configureBlocking(false)

4. Selector.open()

5. ServerSocketChannel 注册 OP_ACCEPT

6. selector.select()

7. 获取 selectedKeys

8. Iterator 遍历

9. isAcceptable()
      ↓
   accept()
      ↓
   得到 SocketChannel
      ↓
   设置非阻塞
      ↓
   注册 OP_READ

10. isReadable()
       ↓
    key.channel()
       ↓
    read(buffer)
       ↓
    flip()
       ↓
    处理数据

11. read == -1
       ↓
    key.cancel()
    channel.close()
```

重点是记流程，不是死背完整代码。

---

## 二十一、常见错误

### 1. 忘记 iterator.remove()

错误：

```java
while (iterator.hasNext()) {

    SelectionKey key = iterator.next();

    // 忘记 iterator.remove()

    ...
}
```

问题：

```text
已经处理过的 key 仍可能留在 selectedKeys
```

所以一般要：

```java
iterator.remove();
```

---

### 2. Channel 没设非阻塞就 register

错误：

```java
SocketChannel socketChannel =
        serverSocketChannel.accept();

socketChannel.register(
        selector,
        SelectionKey.OP_READ
);
```

正确：

```java
socketChannel.configureBlocking(false);

socketChannel.register(
        selector,
        SelectionKey.OP_READ
);
```

记住：

```text
先 non-blocking
再 register
```

---

### 3. key.cancel() 后继续使用 key

错误示例：

```java
if (read == -1) {
    key.cancel();
    socketChannel.close();
}

if (key.isWritable()) {
    ...
}
```

此时 key 已经失效，再调用依赖其有效状态的方法，可能抛：

```text
CancelledKeyException
```

可以：

```java
key.cancel();
socketChannel.close();
continue;
```

或者保证后面不再使用这个 key。

---

### 4. 把 read == -1 当成普通失败

```text
read == -1
```

不是简单的“读取失败”，而是：

> 对方已经正常关闭连接，到达字节流末尾。

---

### 5. 忘记 flip()

错误：

```java
socketChannel.read(buffer);

System.out.println(
        StandardCharsets.UTF_8.decode(buffer)
);
```

正确：

```java
socketChannel.read(buffer);

buffer.flip();

System.out.println(
        StandardCharsets.UTF_8.decode(buffer)
);
```

记忆：

```text
Channel → Buffer
写数据

↓

flip()

↓

Buffer → 程序
读数据
```

---

## 二十二、为什么目前先不深入 OP_WRITE？

`OP_WRITE` 比 `OP_READ` 更容易写错。

Socket 大多数时候本来就是可写的。如果长期注册：

```java
SelectionKey.OP_WRITE
```

Selector 可能频繁返回“可写”，导致循环空转，CPU 占用很高。

实际开发中通常是：

```text
确实还有数据没写完
    ↓
才注册 OP_WRITE

全部写完
    ↓
取消 OP_WRITE
```

这会进一步涉及：

```text
interestOps
连接状态
发送缓冲区
attachment
事件状态管理
```

目前先把：

```text
OP_ACCEPT
OP_READ
```

掌握好即可。

---

## 二十三、建议练习顺序

### 练习 1：只接受客户端连接

目标：

```text
客户端一连接
服务器打印客户端地址
```

只用：

```text
ServerSocketChannel
Selector
OP_ACCEPT
```

### 练习 2：接受连接 + 读取数据

增加：

```text
SocketChannel
OP_READ
ByteBuffer
read()
flip()
```

### 练习 3：两个客户端同时连接

目标：

```text
Client A 发消息 → 服务器能收到

Client B 发消息 → 服务器也能收到
```

重点体会：

> **一个服务器线程可以同时管理多个 TCP 连接。**

---

## 二十四、这一节至少要能回答的问题

```text
为什么需要 Selector？

ServerSocketChannel 和 SocketChannel 有什么区别？

为什么 configureBlocking(false)？

register() 是干什么？

OP_ACCEPT 和 OP_READ 是什么意思？

SelectionKey 从哪里来？

selectedKeys() 里面是什么？

为什么 iterator.remove()？

read() 返回 -1 是什么？

key.cancel() 是什么？

ByteBuffer 为什么 read 后要 flip()？
```

这些大部分能自己讲出来，就可以继续往后学习。

---

## 二十五、最终记忆口诀

```text
ServerSocketChannel 负责接客。

SocketChannel 负责聊天。

Selector 负责盯事件。

SelectionKey 是注册凭证。

register 就是登记 + 订阅事件。

OP_ACCEPT 是有人来。

OP_READ 是有人发消息。

selectedKeys 是当前要处理的事件。

iterator.remove 是处理完就从本轮集合删掉。

read == -1 是对方断开。

key.cancel 是取消 Selector 注册。

Channel read 到 Buffer 后，
要 flip 才能从 Buffer 里读。
```

---

## 二十六、最核心的一句话

> **Java NIO 非阻塞服务器，本质就是：一个线程通过 Selector 监听多个 Channel，哪个 Channel 的事件准备好了，就处理哪个。**

理解这句话之后，再看 `Selector + SelectionKey + SocketChannel`，就不应该再把它们当成一堆互不相关的 API。
