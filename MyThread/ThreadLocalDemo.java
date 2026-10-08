package Socket.MyThread;

public class ThreadLocalDemo {
    private static final ThreadLocal<Person> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) {
        threadLocal.set(new Person("jide"));
        System.out.println(threadLocal.get().name);

        Person x = threadLocal.get();
        x.setName("xiong");

        System.out.println(threadLocal.get().name);
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
