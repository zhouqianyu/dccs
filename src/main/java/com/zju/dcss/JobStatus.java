package com.zju.dcss;

public enum  JobStatus {
    READY(1),NOT_READY(2),FINISH(3),EXCEPTION(4);
    private int index;

    JobStatus(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
