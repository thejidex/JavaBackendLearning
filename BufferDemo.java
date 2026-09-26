package Socket;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class BufferDemo {
    public static void main(String[] args) throws IOException {
        FileChannel source = FileChannel.open(Paths.get("a.txt"), StandardOpenOption.READ);
        FileChannel des = FileChannel.open(Paths.get("b.txt"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        source.transferTo(0, source.size(), des);
    }
}
