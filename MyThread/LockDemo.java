package Socket.MyThread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                add();
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                add();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("cnt=" + cnt);
    }

    private static int cnt = 0;
    private static final Lock lock = new ReentrantLock();

    public static void add() {
        lock.lock();
        try {
            cnt++;
        } finally {
            lock.unlock();
        }
    }
}
