package com.zju.dcss;

import java.util.HashMap;

public class JobContext extends HashMap<Object,Object>{
    int status = 0;
    int workId;
    public JobContext(){
        this(JobStatus.READY.getIndex());
    }

    /**
     * 创建任务数据缓冲
     * @param status 当前任务状态
     */
    public JobContext(int status){
        this.status = status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setWorkId(int workId) {
        this.workId = workId;
    }

    public int getWorkId() {
        return workId;
    }
}
