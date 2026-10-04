package Socket.MyThread;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantDemo {

    private final static ReentrantLock lock = new ReentrantLock();

    private static void f1() {
        lock.lock();
        try {
            System.out.println("get into A, cnt=" + lock.getHoldCount());
            f2();
            System.out.println("return from B, cnt=" + lock.getHoldCount());
        } finally {
            lock.unlock();
        }
    }

    private static void f2() {
        lock.lock();
        try {
            System.out.println("get into B, cnt=" + lock.getHoldCount());
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        f1();
        System.out.println("all down, cnt=" + lock.getHoldCount());
    }
}
