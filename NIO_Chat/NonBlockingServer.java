package Socket.NIO_Chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class NonBlockingServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8888));
        serverSocketChannel.configureBlocking(false);

        // 创建选择器
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    // 获取客户端连接
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
                if (key.isReadable()) {
                    SocketChannel socketCHannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int read = socketCHannel.read(buffer);
                    if (read != -1) {
                        buffer.flip();
                        System.out.println(StandardCharsets.UTF_8.decode(buffer));
                        buffer.clear();
                    } else {
                        System.out.println("客户端已断开");
                        key.cancel();
                        socketCHannel.close();
                        continue;
                    }
                }

                if (key.isWritable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.wrap("你好，客户端".getBytes());
                    client.write(buffer);
                }
            }
        }
    }
}
