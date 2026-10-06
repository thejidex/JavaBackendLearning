package Socket.MyThread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapDemo {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap<>();

        String a = map.putIfAbsent(1, "one");
        String b = map.putIfAbsent(1, "two");
        String c = map.put(1, "three");
        String d = map.put(1, "four");

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
        System.out.println(map.get(1));

        String e = map.computeIfAbsent(
                1,
                key -> {
                    return "five";
                }
        );
        System.out.println(e);
        System.out.println(map.get(1));
    }
}
