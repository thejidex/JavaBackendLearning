package Socket.NIO_Chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class test {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel =ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8888));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(true){
            if(selector.select()!=0){
                Set<SelectionKey> selectionkeySet=selector.selectedKeys();
                Iterator<SelectionKey> iterator=selectionkeySet.iterator();

                while(iterator.hasNext()){
                    SelectionKey x=iterator.next();
                    if(x.isAcceptable()){
                        System.out.println("成功接收到一个客户端");
                    }else if(x.isReadable()){
                        // 处理读取数据事件
                    }else if(x.isWritable()){
                        // 处理写入数据事件
                    }
                }
            }
        }
    }
}
