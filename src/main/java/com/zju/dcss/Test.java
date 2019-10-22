package com.zju.dcss;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public class Test {
    public static void main(String[] args) {
//        try {
//            JobContext context = new JobContext();
//            context.put("input", 5);
//            Worker worker = (Worker) Naming.lookup("rmi://localhost:1101/server3");
//            worker.setJob(new MyJob());
//            worker.doJob(context);
//            try {
//                Thread.sleep(1000);
//                worker.setJob(new getJob());
//                JobContext context1 = new JobContext();
//                JobContext context2 = new JobContext();
//                context2 = worker.doJob(context1);
//                System.out.println(context2.get("output"));
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        } catch (NotBoundException | MalformedURLException | RemoteException e) {
//            e.printStackTrace();
//        }


//        JobDispatcher dispatcher = new JobDispatcher();
//        JobContext[] inputs = new JobContext[5];
//        for (int i = 0; i < inputs.length; ++i) {
//            inputs[i] = new JobContext();
//            inputs[i].put("input", i+1);
//        }
//        JobContext[] resultContests = dispatcher.dispatch(new MyJob(), inputs);
//        for (int i = 0; i < inputs.length; ++i) {
//            System.out.println(resultContests[i].get("output"));
//        }
//        Cache cache = new CacheHandler();
//        cache.remove("hi1");
//        cache.put("5", "1");
//        System.out.println(cache.get("5"));

        FileClient fc = FileClient.connect("localhost", 3010);
//        System.out.println(fc.list("/usr"));
        System.out.println(fc.put("/Users/zhouqianyu/Desktop/hadoop_study/files/name.data",
                "/usr/zhouqianyu"));
    }
}