package Socket.MyThread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueDemo {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(3);
        queue.put(3);
        queue.put(3);
        queue.put(3);
        System.out.println("已经放了三个");
        queue.put(4);
        System.out.println("第四个已放入");
    }
}
