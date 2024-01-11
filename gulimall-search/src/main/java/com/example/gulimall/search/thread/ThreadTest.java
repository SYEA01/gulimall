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
         * - *4）、线程池 【 ExecutorService接口 】
         *      给线程池直接提交任务。
         *         // 当前系统中线程池只有一个，每个异步任务提交给线程池，让它自己去执行
         *         ExecutorService executorService = Executors.newFixedThreadPool(10);
         *
         *         // executorService.execute(new Runnable01());
         *         Future<Integer> future = executorService.submit(new Callable01());
         *         Integer res = future.get();
         *         System.out.println("res = " + res);
         *      1、创建线程池
         *          方式1）、Executors 工具类
         *          方式2）、原生方式：ThreadPoolExecutor executor = new ThreadPoolExecutor()
         *      2、Future：可以获取到异步结果
         *
         * 区别：
         *      1、2方式不能得到返回值。3可以得到返回值
         *      1、2、3 都不能达到资源控制的效果
         *      4 可以控制资源， 好处：性能稳定。
         */

        /**
         * 7大参数：
         *      - int corePoolSize,
         *          核心线程数【只要线程池不销毁就会一直存在】（线程池创建好以后就准备就绪的线程数量，就等待来接收异步任务去执行）   相当于new了 n 个Thread
         *      - int maximumPoolSize,
         *          最大线程数量；控制资源
         *      - long keepAliveTime,
         *          存活时间；如果当前的线程数量大于核心线程数量。  只要线程空闲大于指定的存活时间，就释放空闲的线程（最大线程数量-核心线程数 超出核心线程数的线程的存活时间 ）
         *      - TimeUnit unit,
         *          存活时间的单位
         *      - BlockingQueue<Runnable> workQueue,
         *          阻塞队列。如果任务有很多，比最大线程数量还多，多余出来的任务就会放在阻塞队列中，只要有线程空闲了，就会从阻塞队列中取出来新的任务继续执行
         *      - ThreadFactory threadFactory,
         *          线程的创建工厂。
         *      - RejectedExecutionHandler handler
         *          如果阻塞队列满了，按照指定的拒绝策略拒绝执行任务
         * 工作顺序：
         *      - 1、线程池创建，准备好core数量的核心线程，准备接受任务。
         *      - 2、新的任务进来，用core准备好的空闲线程执行。
         *        - 1）、core满了，就将再进来的任务放入阻塞队列中。空闲的core就会自己去阻塞队列获取任务执行；
         *        - 2）、阻塞队列满了，就直接开新线程执行，最大只能开到max指定的数量；
         *        - 3）、max都执行好了，max减去core数量空闲的线程会在存活时间指定的时间后自动销毁。最终保持在core大小；
         *        - 4）、如果线程开到了max的数量，还有新任务进来，就会使用reject指定的拒绝策略进行处理
         *      - 3、所有的线程创建都是由指定的factory创建的。
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5,  // 核心线程数量5
                200,    //最大线程数量200
                10,  // 最大存活时间10秒
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100000),  // new LinkedBlockingQueue<>() 这个队列默认可以保存Integer的最大值个线程，可能会导致系统内存不够，需要指定数量
                Executors.defaultThreadFactory(),  // 默认的线程工厂
                new ThreadPoolExecutor.AbortPolicy());  // 丢弃策略

//        Executors.newCachedThreadPool();  核心线程数量是0，所有都可以回收
//        Executors.newFixedThreadPool(5);  固定大小，核心线程数量等于最大线程数量，都不可回收
//        Executors.newScheduledThreadPool(5);  定时任务的线程池
//        Executors.newSingleThreadExecutor()  单线程的线程池，后台从队列里边获取任务，挨个执行


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
