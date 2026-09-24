package Socket;

import java.io.*;
import java.net.Socket;

public class ClientSocket {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8888);) {
            System.out.println("===========begin===========");

            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            for (int i = 1; i <= 10; i++) {
                System.out.printf("这是第%d次:", i);
                int x = getRandom();
                int y = getRandom();
                pw.println("add " + x + " " + y);
                String res = br.readLine();
                int sum = Integer.parseInt(res);
                System.out.printf("%d+%d=%d\n", x, y, sum);
            }

            System.out.println("===========end============");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getRandom() {
        return (int) (Math.random() * 100000) % 1000;
    }
}
