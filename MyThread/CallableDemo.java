package Socket.MyThread;

import java.util.concurrent.*;

public class CallableDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        test3();
    }

    public static void test1() throws ExecutionException, InterruptedException, TimeoutException {
        System.out.println("main->提交任务");
        Callable<Integer> task = () -> {
            System.out.println("开始计算");
            Thread.sleep(3000);
            System.out.println("计算结束");
            return 11;
        };
        ExecutorService pool = Executors.newFixedThreadPool(5);
        Future<Integer> future = pool.submit(task);
        System.out.println("提交完了");
        int res = future.get(10, TimeUnit.SECONDS);
        System.out.println("res=" + res);
        pool.shutdown();
    }

    public static void test2() throws ExecutionException, InterruptedException {
        Callable<Integer> callable = () -> {
            System.out.println("子线程开始计算");
            Thread.sleep(3000);
            System.out.println("计算结束");
            return 11;
        };
        FutureTask<Integer> futureTask = new FutureTask<>(callable);
        new Thread(futureTask).start();
        System.out.println("main-》继续执行");
        int res = futureTask.get();
        System.out.println("res=" + res);


    }

    public static void test3() throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask<>(new kk());
        new Thread(futureTask).start();
        int res = futureTask.get();
        System.out.println("res=" + res);
    }
}

class kk implements Callable<Integer> {
    @Override
    public Integer call() {
        return 12;
    }
}
