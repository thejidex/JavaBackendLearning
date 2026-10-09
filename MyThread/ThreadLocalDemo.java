package Socket.MyThread;

public class ThreadLocalDemo {
    private static final ThreadLocal<Person> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println(threadLocal.get());
        new Thread(()->{
            threadLocal.set(new Person("jack"));
            System.out.println(threadLocal.get().name);
        }).start();
        Thread.sleep(2000);
        System.out.println(threadLocal.get());

        new Thread(()->{
            System.out.println(threadLocal.get());
        }).start();
    }
}

class Person {
    public String name;

    public void setName(String name) {
        this.name = name;
    }

    public Person(String x) {
        name = x;
    }
}
