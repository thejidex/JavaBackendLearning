package Socket.MyThread;

import java.util.concurrent.locks.LockSupport;

public class LockSupportDemo {
    public static volatile boolean flag = false;

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            while (!flag) {
                System.out.println("条件不成立，开始park");
                LockSupport.park();
            }
            System.out.println("条件成立，开始干活");
        });
        thread.start();
        Thread.sleep(1000);
        flag = true;
        LockSupport.unpark(thread);
    }
}
