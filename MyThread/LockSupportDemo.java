package Socket.MyThread;

import java.util.concurrent.locks.LockSupport;

public class LockSupportDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println("子线程：我开始运行了");
            System.out.println("子线程：我要被挂起了");
            LockSupport.park();
            // park()阻塞的是调用它的线程
            System.out.println("子线程：我又重新运行了");
        });
        thread.start();
        System.out.println("主线程开始等待");
        Thread.sleep(3000);
        System.out.println("等待结束");
        System.out.println("main：我要重新开始运行子线程了");
        LockSupport.unpark(thread);
        // 此处唤醒的是传入的线程参数
    }
}
