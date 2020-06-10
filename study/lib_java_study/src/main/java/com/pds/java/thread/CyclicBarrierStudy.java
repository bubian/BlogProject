package com.pds.java.thread;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author: pengdaosong
 * CreateTime:  2020-06-10 15:50
 * Email：pengdaosong@medlinker.com
 * Description:字面意思回环栅栏，通过它可以实现让一组线程等待至某个状态之后再全部同时执行。叫做回环是因为当所有等待线程都被释放以后，CyclicBarrier可以被重用。
 * 即:N个线程相互等待，任何一个线程完成之前，所有的线程都必须等待。CyclicBarrier是当await的数量到达了设置的数量的时候，才会继续往下面执行，CyclicBarrier计数达到指定值时，计数置为0重新开始。
 */
public class CyclicBarrierStudy {
    static final Integer NUM = 5;
    public static void main(String[] args) throws InterruptedException {
        //实例CyclicBarrier对象
        CyclicBarrier cyclicBarrier = new CyclicBarrier(NUM);
        //实例化一个固定大小线程池
        Executor executor = Executors.newFixedThreadPool(NUM);
        for(int i = 1;i<=NUM;i++){
            //执行线程
            executor.execute(new MyRunnale(cyclicBarrier,i+"号"));
            //为了更好的效果，休眠一秒
            Thread.sleep(1000);
        }
        System.out.println("指令通知完成");
    }
}

class MyRunnale implements Runnable{
    private CyclicBarrier cyclicBarrier;
    private String mark;
    public MyRunnale(CyclicBarrier cyclicBarrier,String mark) {
        super();
        this.cyclicBarrier = cyclicBarrier;
        this.mark = mark;
    }
    @Override
    public void run() {
        System.out.println(mark+"进入线程,线程阻塞中...");
        try {
            // barrier的await方法，在所有参与者都已经在此 barrier 上调用 await 方法之前，将一直等待。
            cyclicBarrier.await();
            Thread.sleep(6000);//为了看到更好的效果，线程阻塞两秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        System.out.println(mark+"线程阻塞结束,继续执行...");

    }
    public CyclicBarrier getCyclicBarrier() {
        return cyclicBarrier;
    }

    public void setCyclicBarrier(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }

}
