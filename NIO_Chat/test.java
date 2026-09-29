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
        Book book = new Book("曜神", 119);
        writerx.setBook(book);
        Writer writery = (Writer) writerx.clone();

        System.out.println("浅拷贝后：");
        System.out.println("writerx: " + writerx);
        System.out.println("writery: " + writery);

        Book book2 = writery.getBook();
        book2.setName("归虚梦演");

        System.out.println("修改后：");
        System.out.println("writerx: " + writerx);
        System.out.println("writery: " + writery);
    }
}

class Book implements Cloneable {
    public String name;
    public int price;

    public Book(String name, int price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return super.toString() + "{bookName='" +
                name + "', price=" + price + "}";
    }

    public void setName(String s) {
        name = s;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

class Writer implements Cloneable {
    public int age;
    public String name;
    public Book book;

    public Writer(int age, String name) {
        this.name = name;
        this.age = age;
    }

    public void setBook(Book book1) {
        book = book1;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Book getBook() {
        return book;
    }

    @Override
    public String toString() {
        return super.toString() + "{" +
                "age=" + age +
                ", name='" + name + "," + "book=" + book + "'}";
    }

    @Override
    protected Writer clone() throws CloneNotSupportedException {
        Writer writer = (Writer) super.clone();
        writer.setBook((Book) writer.getBook().clone());
        return writer;
    }
}