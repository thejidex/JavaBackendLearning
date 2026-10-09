package Socket.MyThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPoolDemo {
    private static final int N=5;
    private static final ExecutorService exec= Executors.newFixedThreadPool(N);

    public static void main(String[] args) {
        Callable<Double>task=new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                return Math.random()*1000;
            }
        };

        List<Future<Double>> list=new ArrayList<>();
        for(int i=0;i<10;i++){
            list.add(exec.submit(task));
        }

        for(Future<Double>res:list){
            try{
                System.out.println("res="+res.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        exec.shutdown();
        System.out.println("线程池已关闭");
    }
}
