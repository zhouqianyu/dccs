package com.zju.dcss;

public class MyJob implements Job{
    @Override
    public JobContext doTask(JobContext context) {
        int tmp = (int) context.get("input");
        context.put("output", tmp + 1);
        return context;
    }
}
