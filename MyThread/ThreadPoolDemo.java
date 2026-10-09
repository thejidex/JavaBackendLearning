package Socket.MyThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPoolDemo {
    private static final int N = 5;
    private static final ExecutorService exec = Executors.newFixedThreadPool(N);

    public static void main(String[] args) {
        exec.submit(() -> {
            System.out.println("Start");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("End");
        });
        System.out.println("Main thread continue to execute other operations.");
        System.out.println(Thread.currentThread().getName() + " Thread is working");
        exec.shutdown();
    }
}
