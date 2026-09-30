package Socket.MyThread;

public class StatusDemo {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            System.out.println("Hello World!");
        });
        System.out.println(thread.getState());
        thread.start();
        System.out.println(thread.getState());
    }
}
