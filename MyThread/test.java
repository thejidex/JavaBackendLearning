package Socket.MyThread;

public class test {
    public static int t = 0;

    public static void main(String[] args) throws InterruptedException {
        int n = 3;
        int cnt = 100000;
        Thread[] threads = new Thread[n];
        for (int i = 0; i < n; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < cnt; j++) {
                    t++;
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("t=" + t);
    }
}
