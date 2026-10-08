package Socket.MyThread;

public class ThreadLocalDemo {
    private static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            threadLocal.set(100);
            System.out.println(Thread.currentThread().getName() + ":" + threadLocal.get());
        });

        Thread t2 = new Thread(() -> {
            threadLocal.set(200);
            System.out.println(Thread.currentThread().getName() + ":" + threadLocal.get());
        });

        t1.start();
        t2.start();
    }
}
