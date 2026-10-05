package Socket.MyThread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

public class ParkDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("First park");
            LockSupport.park();
            System.out.println("First park end");

            System.out.println("Second park");
            LockSupport.park();
            System.out.println("Second park end");

            System.out.println("Third park");
            LockSupport.park();
            System.out.println("Third park end");
        });
        thread.start();

        System.out.println("主线程开始unpark");
        LockSupport.unpark(thread);
        LockSupport.unpark(thread);
        Thread.sleep(3000);
        System.out.println("主线程再次unpark");
        LockSupport.unpark(thread);
    }
}
