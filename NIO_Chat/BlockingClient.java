package Socket.NIO_Chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class BlockingClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketCHannel = SocketChannel.open();
        socketCHannel.connect(new InetSocketAddress("localhost", 8888));
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put("Hello! I am jide. I love xfs.".getBytes());
        buffer.flip();
        socketCHannel.write(buffer);
        buffer.clear();
        socketCHannel.close();
    }
}
