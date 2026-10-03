package Socket.MyThread;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class test {
    public static void main(String[] args) throws InterruptedException {
        Counter counter=new Counter();
    }
}

class Counter {
    private int cnt = 0;
    private final Lock lock = new ReentrantLock();

    synchronized
    public void add() {
        lock.lock();
        try {
            cnt++;
        } finally {
            lock.unlock();
        }
    }
}
