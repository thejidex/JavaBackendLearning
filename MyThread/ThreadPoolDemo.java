package Socket.MyThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPoolDemo {

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            System.out.println("submitter=" + Thread.currentThread().getName());

            for (String id : new String[]{"A", "B", "C", "D", "E", "F"}) {
                Runnable task = () -> {
                    System.out.println("task-" + id + " on " + Thread.currentThread().getName());
                };
                pool.execute(task);
//                task.run();
            }

            System.out.println("submitted all on " + Thread.currentThread().getName());
        } finally {
            pool.shutdown();
        }
    }
}
