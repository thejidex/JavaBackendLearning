package Socket.MyThread;

import java.util.concurrent.locks.LockSupport;

public class InterruptDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println("开始park()");
            LockSupport.park();
            System.out.println("结束park()");
            System.out.println("是否被中断：" + Thread.currentThread().isInterrupted());
        });
        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
    }
}
