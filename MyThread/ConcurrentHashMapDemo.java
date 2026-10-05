package Socket.MyThread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapDemo {
    public static void main(String[] args) throws InterruptedException {
        Map<Integer, String> map = new ConcurrentHashMap<>();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            int id = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    int key = id * 100 + j;
                    map.put(key, Thread.currentThread().getName());
                }
            }, "Thread-" + i);
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println(map.size());
    }
}
