package com.zju.dcss;


class Server {
    private String name;
    private String host;
    private int port;
    private int workers;
    private boolean isAlive = false;
    private Object serverStub = null;
    private String tmp = "";
    private String dir = "";
    private String log = "";
    private boolean diskPersistent = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    int getWorkers() {
        return workers;
    }

    void setWorkers(int workers) {
        this.workers = workers;
    }

    String getHost() {
        return host;
    }

    void setHost(String host) {
        this.host = host;
    }

    int getPort() {
        return port;
    }

    void setPort(int port) {
        this.port = port;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void connect() {

        Object remote = RemoteBeanContext.getBean(host, port, name);
        if (remote != null) {
            isAlive = true;
        } else {
            isAlive = false;
        }
        serverStub = remote;
    }

    public Object getServerStub() {
        return serverStub;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public boolean isDiskPersistent() {
        return diskPersistent;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public void setDiskPersistent(boolean diskPersistent) {
        this.diskPersistent = diskPersistent;
    }
}
