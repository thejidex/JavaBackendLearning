package Socket.MyThread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapDemo {
    private final ConcurrentHashMap<String, Integer> visitCntMap;

    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMapDemo x = new ConcurrentHashMapDemo();

        x.userVisit("jide");
        x.userVisit("jide");
        x.userVisit("xiong");
        x.userVisit("xiaomei");

        System.out.println("jide=" + x.getUserVisit("jide"));
        System.out.println("xiong=" + x.getUserVisit("xiong"));
        System.out.println("ta=" + x.getUserVisit("ta"));
    }

    public void userVisit(String name) {
        visitCntMap.compute(
                name,
                (key, value) ->
                        value == null ? 1 : value + 1
        );
    }

    public int getUserVisit(String name) {
        return visitCntMap.getOrDefault(name, 0);
    }

    public ConcurrentHashMapDemo() {
        this.visitCntMap = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, Integer> getVisitCntMap() {
        return visitCntMap;
    }
}
