package Socket;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class TheNio {
    public static void main(String[] args) throws IOException {
        Path path = Path.of("test.txt");
        Files.writeString(path, "Hello! I am try to test FileDemo\n" +
                "jack,xiaomei,wutianzi\n" +
                "xiongfengshun\n" +
                "zjide\n");
//        List<String> list = Files.readAllLines(path);
//        for (String line : list) {
//            System.out.println(line);
//        }

        Files.walkFileTree(
                Path.of("D:\\Code\\coding_everyday\\src\\Socket"),
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(
                            Path file,
                            BasicFileAttributes attributes
                    ) {
                        String name = file.toString();
                        if (name.endsWith(".java"))
                            System.out.println(file);
                        return FileVisitResult.CONTINUE;
                    }
                }
        );
    }
}
