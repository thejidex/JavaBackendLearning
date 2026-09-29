package Socket;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectDemo {
    public static void main(String[] args) throws Exception {
        // 获取类
        Class<?> clazz = Class.forName("Socket.Person");
        // 获取构造器
        Constructor<?> constructor = clazz.getConstructor(String.class, int.class);
        // 获取函数
        Method sayHello = clazz.getMethod("sayHello", String.class);
        // 创建对象
        Object x = constructor.newInstance("jide", 20);
        // 调用函数
        sayHello.invoke(x, "Hello everyone!");
        // 找私密成员
        Field name = clazz.getDeclaredField("name");
        name.setAccessible(true);
        // 读取name
        System.out.println(name.get(x));
        // 修改私密成员
        name.set(x, "小熊");
        System.out.println(x);
        // 找private secret()方法
        Method secret = clazz.getDeclaredMethod("secret");
        secret.setAccessible(true);
        // 调用
        secret.invoke(x);

        Method work=clazz.getMethod("work");
        work.invoke(x);
    }
}

class Person {
    private String name;
    private int age;

    public Person() {
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void sayHello(String s) {
        System.out.println(name + ":'" + s + "'");
    }

    public void work() {
        System.out.println("I am " + name + ", I am working, it make me stronger!");
    }

    private void secret() {
        System.out.println("我是私有方法");
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "',age=" + age + "}";
    }
}