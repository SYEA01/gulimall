package com.example.gulimall.search.thread;

/**
 * 创建和初始化多线程的4种方式
 */
public class ThreadTest {
    public static void main(String[] args) {
        System.out.println("main...start...");
        /**
         * - 1）、继承Thread
         *         Thread01 thread = new Thread01();
         *         thread.start();  // 启动线程
         * - 2）、实现Runnable接口
         * - 3）、实现Callable 接口 + FutureTask（可以拿到返回结果，可以处理异常）
         * - 4）、线程池
         */

        Runnable01 runnable01 = new Runnable01();
        new Thread(runnable01).start();

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

}
