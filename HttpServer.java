package Socket;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        try (java.net.ServerSocket serverSocket = new java.net.ServerSocket(8888);) {
            System.out.println("==========begin==========");
            Socket socket = serverSocket.accept();
            System.out.println("已成功接收到一个客户");

            // http://localhost:8888
            // http://localhost:8888/hello
            // 获取读写方式
            HttpTask httpTask = new HttpTask(socket);
            new Thread(httpTask).start();

            System.out.println("==========end===========");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleRequestLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        String[] strs = line.split(" ");
        assert strs.length == 3;
        System.out.println("Method=" + strs[0]);
        System.out.println("Uri=" + strs[1]);
        System.out.println("Version=" + strs[2]);
        System.out.println();
    }

    public static void handleHeader(BufferedReader br) throws IOException {
        String line;
        Map<String, String> mp = new HashMap<>();
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] strs = line.split(":", 2);
            mp.put(strs[0].trim(), strs[1].trim());
            System.out.println(line);
        }
    }

    public static void handleResponse(OutputStream os) throws IOException {
        String version = "HTTP/1.1";
        String status = "200";
        String des = "OK";
        String StatusLine = version + " " + status + " " + des + "\r\n";
        String ContentType = "Content-Type: text/plain; charset=UTF-8\r\n";
        String ContentLength = "Content-Length: 11\r\n";
        String Connection = "Connection: close\r\n";
        String body = "Hello HTTP!";
        String response = StatusLine + ContentType + ContentLength + Connection + "\r\n" + body;
        byte[] res = response.getBytes();
        os.write(res);
        os.flush();
        os.close();
    }
}
