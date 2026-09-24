package Socket;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        try (java.net.ServerSocket serverSocket = new java.net.ServerSocket(8888);) {
            System.out.println("==========begin==========");

            // http://localhost:8888
            // http://localhost:8888/hello
            // 获取读写方式

            int cnt = 0;
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new HttpTask(socket)).start();
//                System.out.printf("这是第%d个客户\n", ++cnt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
