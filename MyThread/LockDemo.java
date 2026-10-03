package Socket.MyThread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockDemo {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    lock.lock();
                    try {
                        System.out.println(Thread.currentThread().getName() + "获得锁");
                    } finally {
                        lock.unlock();
                    }
                    try{
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.start();
        }
    }

    private static final ReentrantLock lock = new ReentrantLock(false);
}
