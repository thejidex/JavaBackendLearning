package Socket.MyThread;

public class test {
    public static void main(String[] args) throws InterruptedException {
        myRun k = new myRun();

        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(k);
            thread.start();
            threads[i] = thread;
        }
        for (int i = 0; i < 5; i++) {
            threads[i].join();
        }

        System.out.println(myRun.t);
    }
}

class myRun implements Runnable {
    public static int t = 0;

    public synchronized static void increase() {
        System.out.println(Thread.currentThread().getName());
        t++;
    }
    // 同步对象 可重入性 同步代码块

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            increase();
        }
    }
}
