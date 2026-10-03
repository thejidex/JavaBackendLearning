package Socket.MyThread;

import java.util.concurrent.locks.ReentrantLock;

public class TicketOffice {
    private static final ReentrantLock lock = new ReentrantLock(true);
    private static int tickets = 20;

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    lock.lock();
                    try {
                        if (tickets > 0) {
                            tickets--;
                            System.out.println(Thread.currentThread().getName() + "出票成功，还剩" + tickets + "张票");
                        } else {
                            break;
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            });
            thread.start();
        }
    }
}
