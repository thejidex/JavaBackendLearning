package Socket.MyThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolDemo {

    public static void main(String[] args) throws InterruptedException {
        MyThreadPool pool = new MyThreadPool(2);
        for (int i = 0; i < 10; i++) {
            int id = i;
            pool.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " is execute task-" + id);
            });
        }
    }
}
