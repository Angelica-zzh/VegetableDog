package com.example.myclock.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtils {
    private static volatile ThreadUtils instance; // = new ThreadUtils();
    ExecutorService pool;

    private ThreadUtils() {
        pool = Executors.newCachedThreadPool();
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
}
