package com.zju.dcss;


import java.io.Serializable;

public interface Job extends Serializable{
    JobContext doTask(JobContext context);
}
