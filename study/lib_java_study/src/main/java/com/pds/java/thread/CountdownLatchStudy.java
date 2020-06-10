package com.pds.java.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author: pengdaosong
 * CreateTime:  2020-06-10 15:39
 * Email：pengdaosong@medlinker.com
 * Description: 一个线程(或者多个)，等待另外N个线程完成某个事情之后才能执行。是并发包中提供的一个可用于控制多个线程同时开始某个动作的类，
 * 其采用的方法为减少计数的方式，当计数减至零时位于latch.Await()后的代码才会被执行，CountDownLatch是减计数方式，计数==0时释放所有等待的线程；
 * CountDownLatch当计数到0时，计数无法被重置；
 */
public class CountdownLatchStudy {
    public static void main(String[] args) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(5);

        // test1(countDownLatch);
        test2(countDownLatch);

    }

    private static void test1(CountDownLatch countDownLatch) throws Exception{
        //测试阻塞其他线程
        new Thread(new MyRunnable(countDownLatch)).start();
        //为了测试效果进行线程休眠
        Thread.sleep(1000);
        for(int i = 1;i<=5;i++){
            countDownLatch.countDown();
            System.out.println("第"+i+"调用countDown方法结束");
            //为了测试效果进行线程休眠
            Thread.sleep(1000);
        }
    }

    private static void test2(CountDownLatch countDownLatch) throws Exception{
        // 测试阻塞主线程
        for (int i = 1; i <= 5; i++) {
            new Thread(new MyRunnable1(countDownLatch,i+"")).start();
            Thread.sleep(1000);
        }
        try {
            System.out.println("主线程阻塞");
            countDownLatch.await();
            System.out.println("主线程继续执行");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


class MyRunnable implements Runnable {
    CountDownLatch countDownLatch;

    public MyRunnable(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            System.out.println("进入线程，即将进入阻塞状态");
            //调用await进行线程阻塞
            countDownLatch.await();
            System.out.println("线程进行执行...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class MyRunnable1 implements Runnable {
    private CountDownLatch countDownLatch;
    private String mark;

    public MyRunnable1(CountDownLatch countDownLatch, String mark) {
        super();
        this.countDownLatch = countDownLatch;
        this.mark = mark;
    }

    @Override
    public void run() {
        System.out.println(mark+"号线程开始");
        try {
            //使线程休眠，看到更好的测试效果
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(mark+"号线程结束");
        //调用CountDownLatch的countDown方法进行次数减1
        countDownLatch.countDown();
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

}
