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

        new Thread(() -> {
            System.out.println("消费者准备开始拿数了");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                Integer value = queue.take();
                System.out.println("消费者已拿走" + value);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        System.out.println("开始放入第四个数");

        queue.put(4);
        System.out.println("第四个已放入");
    }
}
