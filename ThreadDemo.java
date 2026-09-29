package Socket;

public class ThreadDemo {
    public static void main(String[] args) {
        Hero hero1 = new Hero("李伟");
        Hero hero2 = new Hero("小熊");
        Hero hero3 = new Hero("王强");
        hero1.run();
        hero2.run();
        hero3.run();
        new Thread(hero1).start();
        new Thread(hero2).start();
        new Thread(hero3).start();
    }
}

class Hero implements Runnable {
    public String name;

    public Hero(String x) {
        name = x;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(name + "  " + i);
        }
    }
}

//class Person e
