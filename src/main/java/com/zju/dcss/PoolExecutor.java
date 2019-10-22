package com.zju.dcss;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PoolExecutor {
    private static ThreadPoolExecutor pool;
    private static ScheduledThreadPoolExecutor spool;

    static synchronized ThreadPoolExecutor createPoll() {
        if (pool == null) {
            pool = new ThreadPoolExecutor(
                    5,
                    10,
                    100,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(300));
        }
        return pool;
    }

    static synchronized ScheduledThreadPoolExecutor createScheduledPoll() {
        if(spool == null){
            spool = new ScheduledThreadPoolExecutor(3);
        }
        return spool;
    }

    void execute(Runnable task) {
        createPoll().execute(task);
    }

    void execute(Runnable runnable, long delay, long period){
        createScheduledPoll().scheduleWithFixedDelay(runnable, delay, period, TimeUnit.SECONDS);
    }
}
