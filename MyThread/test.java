package Socket.MyThread;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class test {
    private int product = 0;

    public synchronized void put() throws InterruptedException {
        while (product == 1) {
            System.out.println("已经有一个商品了");
            wait();
        }
        product = 1;
        System.out.println(Thread.currentThread().getName() + "生产了一个商品");
        notifyAll();
    }

    public synchronized void take() throws InterruptedException {
        while (product == 0) {
            System.out.println("现在没有商品");
            wait();
        }
        product = 0;
        System.out.println(Thread.currentThread().getName() + "拿走了一个商品");
        notifyAll();
    }

    public static void main(String[] args) throws InterruptedException {
        test x = new test();
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    x.put();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    x.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        producer.start();
        consumer.start();
    }
}
