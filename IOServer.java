package Socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// http://localhost:8889/hello
public class IOServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8889);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("===begin====");

                InputStream is = socket.getInputStream();

                // 1.读取请求行
                String line = readLine(is);
                System.out.println("请求行：");
                System.out.println(line);
                System.out.println();

                // 2.读取请求体
                Map<String, String> headers = new HashMap<>();
                while ((line = readLine(is)) != null) {
                    if (line.isEmpty()) break;

                    System.out.println("请求头：" + line);

                    String[] strs = line.split(":", 2);
                    headers.put(strs[0].trim(), strs[1].trim());
                }
                System.out.println();

                // 3.读取请求信息
                if (headers.containsKey("content-length")) {
                    int len = Integer.parseInt(headers.get("content-length"));
                    byte[] data = new byte[0];
                    data = readBytes(is, len);

                    System.out.println("请求信息长度：" + len);
                    String message = new String(data, StandardCharsets.UTF_8);
                    System.out.println("请求信息：" + message);
                }

                // 4.返回响应
                String res = "Hello, I am Jack. Do you eat dinner?";
                byte[] message = res.getBytes(StandardCharsets.UTF_8);
                OutputStream os = socket.getOutputStream();
                String responseHeader =
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: text/plain; charset=UTF-8\r\n" +
                                "Content-Length: " +
                                message.length +
                                "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n";
                os.write(responseHeader.getBytes(StandardCharsets.ISO_8859_1));
                os.write(message);
                os.flush();

                socket.close();

                System.out.println("===end=====");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readLine(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;

        while ((b = is.read()) != -1) {
            if (b == '\r') {
                int next = is.read();
                if (next == '\n') break;
                buffer.write(b);
                if (next != -1) {
                    buffer.write(next);
                }
            } else {
                buffer.write(b);
            }
        }

        if (b == -1 && buffer.size() == 0) {
            return null;
        }

        return buffer.toString(StandardCharsets.ISO_8859_1);
    }

    private static byte[] readBytes(InputStream is, int len) throws IOException {
        byte[] data = new byte[len];
        int total = 0;
        while (total < len) {
            int l = is.read(data, total, len - total);
            if (l == -1) {
                throw new EOFException("HTTP Body尚未读取完整，连接就关闭了");
            }
            total += l;
        }
        return data;
    }
}
