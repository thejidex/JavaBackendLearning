package Socket.MyThread;

import java.util.concurrent.locks.LockSupport;

public class LockSupportDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println("子线程：我开始运行了");
            System.out.println("子线程：我要被挂起了");
            LockSupport.park();
            System.out.println("子线程：我又重新运行了");
        });
        thread.start();
        Thread.sleep(3000);
        System.out.println("main：我要重新开始运行子线程了");
        LockSupport.unpark(thread);
    }
}
