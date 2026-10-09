package Socket.MyThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolDemo {

    public static void main(String[] args) {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                2,
                4,
                30,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        try {
            System.out.println(Thread.currentThread().getName() + " begin to submit");

            for (int i = 1; i <= 10; i++) {
                int id = i;
                Runnable task = () -> {
                    System.out.println("task-" + id + " on " + Thread.currentThread().getName());
                };
                try {
                    pool.execute(task);
                    System.out.println("task-" + id + " is Accepted");
                } catch (RejectedExecutionException e) {
                    System.out.println("task-" + id + " is Rejected");
//                    e.printStackTrace();
                }
            }

            System.out.println("all task submitted on " + Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}
