package Socket.MyThread;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentLinkedQueueDemo {
    public static void main(String[] args) {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

        queue.add("C/C++");
        queue.add("Python");
        queue.add("Java");
        queue.add("MySQL");
        // add 满了直接报错

        queue.offer("Redis");
        // offer 满了返回false，不会报错

        System.out.println(queue);

        System.out.println(queue.peek());
        System.out.println(queue.poll());
        System.out.println(queue.poll());
        System.out.println(queue.element());
        System.out.println(queue);

    }
}
