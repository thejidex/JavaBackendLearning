package Socket.MyThread;

import java.util.concurrent.CopyOnWriteArrayList;

public class CopyOnWriteArrayListDemo {
    public static void main(String[] args) {
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
        list.add(1);
        list.add(2);
        for (Integer x : list) {
            System.out.println("x=" + x);
            list.add(3);
        }
        System.out.println(list);
    }
}
