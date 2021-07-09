package com.example.myclock.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadUtils {
    private static volatile ThreadUtils instance; // = new ThreadUtils();
    ExecutorService pool;
    ScheduledExecutorService spool;

    private ThreadUtils() {
        pool = Executors.newCachedThreadPool();
        spool = Executors.newSingleThreadScheduledExecutor();
    }

    public static ThreadUtils getInstance() {
        if (instance == null) {
            synchronized (ThreadUtils.class) {
                if (instance == null) {
                    instance = new ThreadUtils();
                }
            }
        }
        return instance;
    }

    public void execute(Runnable runnable) {
        pool.execute(runnable);
    }
    public void scheduleExecure(Runnable runnable, int delay, int period, TimeUnit timeUnit){
        spool.scheduleWithFixedDelay(runnable,delay,period,timeUnit);

    }
}
