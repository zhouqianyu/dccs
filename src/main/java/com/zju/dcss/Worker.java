package com.zju.dcss;

import java.rmi.RemoteException;
import java.util.List;

public interface Worker extends RemoteServer {
    void setJob(Job job) throws RemoteException;
    List<JobContext> doJob(JobContext context) throws RemoteException;
    List<JobContext> doJob() throws RemoteException;
    JobContext interrupt(int i)throws RemoteException;

}
