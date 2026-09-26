package Socket.NIO_Chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class BlockingServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(8888));
        ssc.configureBlocking(true);

        while (true) {
            SocketChannel socketChannel = ssc.accept();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int read = socketChannel.read(buffer);
            while (read != -1) {
                buffer.flip();
                System.out.println(StandardCharsets.UTF_8.decode(buffer));
                buffer.clear();
                read = socketChannel.read(buffer);
            }
            socketChannel.close();
        }
    }
}
