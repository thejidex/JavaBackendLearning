package Socket.MyThread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapDemo {
    public static void main(String[] args) throws InterruptedException {
        Map<Integer, String> map = new ConcurrentHashMap<>();

        // putIfAbsent 如果不存在key，就创建{key=value}
        // 不管存不存在都会返回原本存在的value
        // 如果不存在的话，value就是null
        String old = map.putIfAbsent(2, "xiong");
        System.out.println("old=" + old);

        String x = map.get(2);
        System.out.println("x=" + x);

        String value = map.putIfAbsent(2, "xfs");
        System.out.println(value);

        x = map.get(2);
        System.out.println("x=" + x);

        String i = map.computeIfAbsent(
                3,
                key -> {
                    System.out.println("hello");
                    return "kk";
                }
        );
        String j = map.get(3);
        System.out.println("i=" + i);
        i = map.computeIfAbsent(
                3,
                key -> {
                    System.out.println("hello");
                    return "kk";
                }
        );
        System.out.println("i=" + i);
    }
}
