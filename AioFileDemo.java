package Socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;

public class AioFileDemo {
    public static void main(String[] args) {
        Path path = Paths.get("a.txt");
        String str = "Hello World!!!";
        writeFile(path, str);
        readFile(path);
    }

    private static void writeFile(Path path, String str) {
        try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            ByteBuffer buffer = StandardCharsets.UTF_8.encode(str);
            Future<Integer> res = fileChannel.write(buffer, 0);
            res.get();
            System.out.println("写入成功");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void readFile(Path path) {
        try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // 调用 fileChannel.read() 方法从文件中异步读取数据。该方法接受一个 CompletionHandler 对象，用于处理异步操作完成后的回调。
            fileChannel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    // 在 CompletionHandler 的 completed() 方法中，翻转 ByteBuffer（attachment.flip()），然后使用 Charset.forName("UTF-8").decode() 将其解码为字符串并打印。最后，清空缓冲区并关闭文件通道。
                    attachment.flip();
                    System.out.println("读取的内容: " + StandardCharsets.UTF_8.decode(attachment));
                    attachment.clear();
                    try {
                        fileChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    // 如果异步读取操作失败，CompletionHandler 的 failed() 方法将被调用，打印错误信息。
                    System.out.println("读取失败");
                    exc.printStackTrace();
                }
            });

            // 等待异步操作完成
            Thread.sleep(1000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
