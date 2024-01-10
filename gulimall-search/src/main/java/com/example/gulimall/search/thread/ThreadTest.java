package com.example.gulimall.search.thread;

import java.util.concurrent.*;

/**
 * 创建和初始化多线程的4种方式
 */
public class ThreadTest {


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main...start...");
        /**
         * - 1）、继承Thread
         *         Thread01 thread = new Thread01();
         *         thread.start();  // 启动线程
         *
         * - 2）、实现Runnable接口
         *         Runnable01 runnable01 = new Runnable01();
         *         new Thread(runnable01).start();
         *
         * - 3）、实现Callable 接口 + FutureTask（可以拿到返回结果，可以处理异常）
         *         FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
         *         new Thread(futureTask).start();
         *         // get() 方法的作用是： 阻塞等待整个线程执行完成，获取返回结果
         *         Integer res = futureTask.get();
         *         System.out.println("res = " + res);
         *
         * - 4）、线程池 【 * 】
         *      给线程池直接提交任务。
         *
         * 区别：
         *      1、2方式不能得到返回值。3可以得到返回值
         *      1、2、3 都不能达到资源控制的效果
         *      4 可以控制资源， 好处：性能稳定。
         */

        // 当前系统中线程池只有一个，每个异步任务提交给线程池，让它自己去执行
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // executorService.execute(new Runnable01());
        Future<Integer> future = executorService.submit(new Callable01());
        Integer res = future.get();
        System.out.println("res = " + res);

        System.out.println("main...end...");

    }

    /**
     * 1）、继承Thread
     */
    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println("当前线程： " + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("Thread01运行结果：" + i);
        }
    }

    /**
     * 2）、实现Runnable接口
     */
    public static class Runnable01 implements Runnable {
        @Override
        public void run() {
            System.out.println("当前线程： " + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("Runnable01运行结果：" + i);
        }
    }

    /**
     * 3）、实现Callable 接口 + FutureTask（可以拿到返回结果，可以处理异常）
     * public static class Callable01 implements Callable<Integer>   这里的泛型指的是返回值的类型
     */
    public static class Callable01 implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程： " + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("Callable01运行结果：" + i);
            return i;
        }
    }

}
