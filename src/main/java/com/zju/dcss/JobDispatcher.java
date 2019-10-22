package com.zju.dcss;

import java.rmi.RemoteException;
import java.util.*;

/**
 * 等会接着调试 还有点问题 在异步那里
 */
public class JobDispatcher extends PoolExecutor{
    public JobContext[] dispatch(Job job, JobContext... contexts) {
        Map<String, Server> slaves = PropHandler.getSlaves();
        List<JobContext> listContext = new ArrayList<>();
        Queue<JobContext> waitingJob = new ArrayDeque<>(Arrays.asList(contexts));
        Worker[] workers = new Worker[slaves.size()];

        try {
            int i = 0;
            for (Map.Entry<String, Server> slave : slaves.entrySet()
            ) {
                workers[i] = (Worker) slave.getValue().getServerStub();
                if (workers[i] != null)
                    workers[i].setJob(job);
                ++i;
            }
            int j = 0;
            //只要等待队列有任务或者回收list里数量不等于输入 就继续跑任务
            while (waitingJob.size() > 0||listContext.size()!=contexts.length) {
                List<JobContext> tmp;
                if(waitingJob.size()==0){
                    tmp = workers[j++].doJob();
                }else {
                    tmp = workers[j++].doJob(waitingJob.poll());
                }
                for (int x = 0; x < tmp.size(); ++x) {
                    JobContext tmpContext = tmp.get(x);
                    if (tmpContext.getStatus() != JobStatus.FINISH.getIndex()) {
                        //把状态不为完成的所有任务拉出来 重新进入等待队列
                        waitingJob.add(tmpContext);
                        tmp.remove(tmpContext);
                    }
                }
                listContext.addAll(tmp);
                System.out.println(listContext);
                if(j>=workers.length) j = 0;
//                Thread.sleep(500);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return listContext.toArray(new JobContext[0]);
    }

    public static void main(String[] args) {
        JobDispatcher dispatcher = new JobDispatcher();
        JobContext[] contexts = new JobContext[1000];
        for (int i = 0; i < 1000; ++i) {
            contexts[i] = new JobContext();
            contexts[i].setWorkId(i);
            contexts[i].put("input", i);
        }
        contexts = dispatcher.dispatch(new MyJob(), contexts);
        for (JobContext context : contexts) {
            System.out.println(context.getWorkId()+" "+context.get("input")+" "+context.get("output"));
        }
    }
}
