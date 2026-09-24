package Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HttpTask implements Runnable {
    private Socket socket;

    public HttpTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            System.out.println("socket cnt't be null");
            throw new IllegalArgumentException("socket is null!!!");
        }
        try (PrintWriter pw = new PrintWriter(socket.getOutputStream());
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 打印开始处理信息
            System.out.println(Thread.currentThread().getName() + "在" + getNowTime() + "开始处理...");

            Request request = new Request();
            request.handleRequest(br);

            System.out.println(Thread.currentThread().getName() + " " + request.getMethod() + " " + request.getUrl());

            // 打印请求HTTP
//            request.print();

            // 中间强制终端三秒
            Thread.sleep(3000);

            // 写给客户端的消息
            String message = "Hello, I am jide!";
            Response response = new Response();
            String res = response.buildResponse(request, message);

            // 写给客户端的HTTP
            pw.print(res);
            pw.flush();

            // 打印处理结束信息
            System.out.println(Thread.currentThread().getName() + "在" + getNowTime() + "处理结束。");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNowTime() {
        LocalTime now = LocalTime.now();
        return now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
