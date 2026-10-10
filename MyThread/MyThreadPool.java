package Socket.MyThread;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MyThreadPool {
    private BlockingQueue<Runnable> queue;
    private List<Thread> workers;

    public MyThreadPool(int size) {
        queue = new ArrayBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            Thread t = new Thread(new worker(), "worker-" + i);
            t.start();
        }
    }

    public void execute(Runnable task) throws InterruptedException {
        queue.put(task);
    }

    class worker implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Runnable task = queue.take();
                    task.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
