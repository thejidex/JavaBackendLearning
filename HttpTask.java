package Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
            Request request = new Request();
            request.handleRequest(br);
            // 打印请求HTTP
            request.print();

            String message = "Hello, I am jide!";
            Response response = new Response();
            String res = response.buildResponse(request, message);
            pw.println(res);
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
