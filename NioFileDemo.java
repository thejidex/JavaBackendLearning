package Socket;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

public class NioFileDemo {
    public static void main(String[] args) {
        Path path = Paths.get("a.txt");
        String s = "Hello! I am jack. Today is a good day. Have a nice day!";
        writeFile(path, s);
        readFile(path);
    }

    private static void writeFile(Path path, String str) {
        try (FileChannel fileChannel = FileChannel.open(path, EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE));) {
            ByteBuffer buffer = StandardCharsets.UTF_8.encode(str);
            fileChannel.write(buffer);
            System.out.println("已写入");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void readFile(Path path) {
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead = fileChannel.read(buffer);
            while (bytesRead != -1) {
                buffer.flip();
                System.out.println("文件内容：" + StandardCharsets.UTF_8.decode(buffer));
                buffer.clear();
                bytesRead = fileChannel.read(buffer);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
