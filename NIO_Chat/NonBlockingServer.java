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
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8888));
        serverSocketChannel.configureBlocking(false);
        Selector selector=Selector.open();
        serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);

        while(true){
            selector.select();
            Iterator<SelectionKey>iterator=selector.selectedKeys().iterator();

            while(iterator.hasNext()){
                SelectionKey key=iterator.next();
                iterator.remove();
                if(key.isAcceptable()){
                    System.out.println("客户端已连接");
                    SocketChannel client=serverSocketChannel.accept();
                    client.configureBlocking(false);
                    client.register(selector,SelectionKey.OP_READ);
                }
            }
        }
    }
}
