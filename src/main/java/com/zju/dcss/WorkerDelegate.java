package com.zju.dcss;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WorkerDelegate extends PoolExecutor {
    private Job job;
    List<JobContext> cacheContext = null;
    Lock lock = new ReentrantLock();
    WorkerDelegate() {
        cacheContext = new ArrayList<>();
    }

    @Delegate(
            interfaceName = "com.zju.dcss.Worker",
            methodName = "setJob"
    )
    void setJob(Job job) {
        this.job = job;
    }

    @Delegate(
            interfaceName = "com.zju.dcss.Worker",
            methodName = "doJob"
    )
    List<JobContext> doJob(JobContext context) throws InterruptedException {
        execute(new Runnable() {
            @Override
            public void run() {
                JobContext ct = new JobContext();
                try {
                    JobContext tmp = job.doTask(context);
                    if (tmp != null) {
                        ct.putAll(tmp);
                        ct.setStatus(JobStatus.FINISH.getIndex());
                    }
                } catch (Exception e) {
                    ct.setStatus(JobStatus.EXCEPTION.getIndex());
                    e.printStackTrace();
                } finally {
                    lock.lock();
                    cacheContext.add(ct);
                    lock.unlock();
                }
            }
        });
        List<JobContext> tmp = new ArrayList<>();
        lock.lock();
        tmp.addAll(cacheContext);
        cacheContext.clear();
        lock.unlock();
        return tmp;
    }

    @Delegate(
            interfaceName = "com.zju.dcss.Worker",
            methodName = "doJob"
    )
    List<JobContext> doJob() {
        return cacheContext;
    }

    @Delegate(
            interfaceName = "com.zju.dcss.Worker",
            methodName = "interrupt"
    )
    JobContext interrupt(int i) {
        return null;
    }

    @Delegate(
            interfaceName = "com.zju.dcss.RemoteServer",
            methodName = "isAlive"
    )
    boolean isAlive() {
        return true;
    }

}
