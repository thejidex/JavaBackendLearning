package Socket.MyThread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockDemo {
    public static void main(String[] args) {

        Thread thread = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("线程1拿到锁");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                System.out.println("线程1释放锁");
                lock.unlock();
            }
        });

        Thread thread1 = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                if (lock.tryLock(3, TimeUnit.SECONDS)) {
                    try {
                        System.out.println("线程2拿到锁了");
                    } finally {
                        lock.unlock();
                    }
                } else {
                    System.out.println("线程2没有拿到锁");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        thread.start();
        thread1.start();
    }

    private static final ReentrantLock lock = new ReentrantLock();
}
