package Socket.MyThread;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionDemo {
    public static void main(String[] args) {
        MyBlockingQueue<Integer> a = new MyBlockingQueue<>(5);
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    a.put(i + 10);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "Producer");
        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    int value = a.take();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "Consumer");
        
        producer.start();
        consumer.start();
    }
}

class MyBlockingQueue<E> {
    private final Queue<E> queue = new ArrayDeque<>();
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

    public MyBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public void put(E element) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();// 等待非满，也就是有空位的时候
            }
            queue.add(element);
            System.out.println(Thread.currentThread().getName() + "放入" + element);
            notEmpty.signal();// 使用 notEmpty 唤醒等待非空的消费者
        } finally {
            lock.unlock();
        }
    }

    public E take() {
        lock.lock();
        try {
            while (queue.size() == 0) {
                notEmpty.await();
            }
            E element = queue.remove();
            System.out.println(Thread.currentThread().getName() + "取出" + element);
            notFull.signal();
            return element;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}