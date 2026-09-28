package Socket.NIO_Chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class test {
    public static void main(String[] args) throws IOException {

    }
}

class TestClone {
    public static void main(String[] args) throws CloneNotSupportedException {
        Writer writerx = new Writer(20, "jide");
        Writer writery = (Writer) writerx.clone();

        System.out.println("浅拷贝后：");
        System.out.println("writerx: " + writerx);
        System.out.println("writery: " + writery);

        writery.setName("xiong");
        System.out.println("修改后：");
        System.out.println("writerx: " + writerx);
        System.out.println("writery: " + writery);
    }
}

class Book {
    public String name;
    public int price;

}

class Writer implements Cloneable {
    public int age;
    public String name;

    public Writer(int age, String name) {
        this.name = name;
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + "{" +
                "age=" + age +
                ", name='" + name + "'}";
    }

    @Override
    protected Writer clone() throws CloneNotSupportedException {
        return (Writer) super.clone();
    }
}