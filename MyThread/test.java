package Socket.MyThread;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class test {
    public static void main(String[] args) throws InterruptedException {
    }
}

class RWDemo {
    private final static ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();
    private final static Lock readLock = rwlock.readLock();
    private final static Lock writeLock = rwlock.writeLock();
    private static String data = "Hello jide!";

    public static void read() {
        readLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "开始读取数据");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + "读取到数据:data=" + data);
        } finally {
            readLock.unlock();
        }
    }

    public static void write(String newData) {
        writeLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "开始修改数据");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            data = newData;
            System.out.println(Thread.currentThread().getName() + "修改完成");
        } finally {
            writeLock.unlock();
        }
    }

    public static void main(String[] args) {
        new Thread(RWDemo::read).start();
        new Thread(RWDemo::read).start();
        new Thread(RWDemo::read).start();
        new Thread(() -> {
            write("World!");
        }).start();
        new Thread(RWDemo::read).start();
        new Thread(RWDemo::read).start();
        new Thread(RWDemo::read).start();
    }
}