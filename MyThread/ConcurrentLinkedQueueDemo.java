package Socket.MyThread;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentLinkedQueueDemo {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();


        Thread[] threads = new Thread[4];
        for (int i = 0; i < 4; i++) {
            int id = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    queue.offer(id * 1000 + j);
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < 4; i++) {
            threads[i].join();
        }

        int cnt = 0;
        while (queue.poll() != null)
            cnt++;

        System.out.println("cnt=" + cnt);
    }
}
