package Socket.MyThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolDemo {

    public static void main(String[] args) {
        ExecutorService exec = new ThreadPoolExecutor(
                2,
                4,
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                new ThreadFactory() {
                    int cnt = 0;

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r, "order-worker-" + ++cnt);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.AbortPolicy()
        );

        System.out.println("Main thread begin to submit tasks.");
        for (int i = 0; i < 20; i++) {
            int id = i;
            Runnable task = () -> {
                System.out.println("订单-" + id + "开始被" + Thread.currentThread().getName() + "处理");
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };

            try {
                exec.execute(task);
            } catch (RejectedExecutionException e) {
                System.out.println("!!!!!  订单-" + id + "提交失败");
            }
        }

        System.out.println("All tasks has been submitted by " + Thread.currentThread().getName());
        System.out.println("即将关闭线程池");
        exec.shutdown();
    }
}
