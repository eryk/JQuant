package net.jquant.common;

import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.*;

public class ParallelProcesser {

    static volatile boolean isInit = false;

    static ScheduledExecutorService executorService;

    static ExecutorService threadPool;

    private static int executor_pool_size = 1;
    private static int schedule_pool_size = 1;

    public static synchronized void init(int scheduledPoolSize,int threadPoolSize) {
        if (executorService == null) {
            executorService = Executors.newScheduledThreadPool(scheduledPoolSize);
        }
        if(threadPool == null){
            threadPool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(threadPoolSize));
        }
        isInit = true;
    }

    public static void close() {
        Utils.closeThreadPool(executorService);
        Utils.closeThreadPool(threadPool);
        isInit = false;
    }

    public static void process(Runnable task){
        if(threadPool == null){
            init(schedule_pool_size,executor_pool_size);
        }
        threadPool.execute(task);
    }

    public static void schedule(Runnable runnable, int start, int period){
        if(executorService == null){
            init(schedule_pool_size,executor_pool_size);
        }
        executorService.scheduleAtFixedRate(runnable,start,period, TimeUnit.MINUTES);
    }
}
