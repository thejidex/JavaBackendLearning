package Socket.MyThread;

import java.util.concurrent.locks.LockSupport;

public class LockSupportDemo {
    public static volatile boolean flag = false;

    private static Thread a;
    private static Thread b;

    public static void main(String[] args) throws InterruptedException {
        a = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("A" + i);
                LockSupport.unpark(b);
                LockSupport.park();
            }
        });
        b = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                LockSupport.park();
                System.out.println("B" + i);
                LockSupport.unpark(a);
            }
        });

        a.start();
        b.start();
    }
}
