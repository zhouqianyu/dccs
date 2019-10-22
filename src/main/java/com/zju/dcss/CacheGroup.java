package com.zju.dcss;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Random;

public class CacheGroup extends PoolExecutor {
    private Map<String, Server> cg;
    private String serviceName = null;
    CacheGroup(String name) {
        cg = PropHandler.getCaches();
        this.serviceName = name;
    }

    /**
     * 根据服务器名字取到服务器信息，名字为null或没有该名字或该服务器非存活状态 则随机返回一台存活的服务器
     *
     * @param name
     * @return
     */
    CacheServer getCacheServer(String name) {
        Server server = null;
        int i = 0;
        if (name != null && (server = cg.get(name)) != null && server.isAlive()) {
        } else {
            String[] keys = cg.keySet().toArray(new String[0]);
            Random random = new Random();
            do {
                String key = keys[random.nextInt(cg.size())];
                server = cg.get(key);
                server.connect();  //所有集群操作前 都要重新尝试连接服务器，以确保其存活
                ++i;
            } while (!server.isAlive()&&i<10); //最大尝试10次
        }
        if(i == 10) return null;
        else return (CacheServer) server.getServerStub();
    }

    /**
     * 放入一台服务器后调用 同步刷新其他服务器的缓存
     */
    void synchronizedPut(String key, Object value) {
        execute(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, Server> server : cg.entrySet()
                ) {
                    server.getValue().connect(); //所有集群操作前 都要重新尝试连接服务器，以确保其存活
                    if (server.getValue().isAlive() && !server.getValue().getName().equals(serviceName)) {
                        CacheServer cacheServer = (CacheServer) server.getValue().getServerStub();
                        try {
                            cacheServer.put(key, value, 0);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    void synchronizedRemove(String key) {
        //TODO....
        execute(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, Server> server : cg.entrySet()
                ) {
                    server.getValue().connect();
                    if (server.getValue().isAlive() && !server.getValue().getName().equals(serviceName)) {
                        CacheServer cacheServer = (CacheServer) server.getValue().getServerStub();
                        try {
                            cacheServer.remove(key);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
     * 一个新的缓存服务器上线后，把自己的数据分享跟集群中其他服务器
     * @param cacheMap
     */
    void spot(CacheMap cacheMap){
        for (Map.Entry<String,Server> entry:cg.entrySet()){
                entry.getValue().connect();
            if(!entry.getValue().getName().equals(this.serviceName)&&entry.getValue().isAlive()){
                CacheServer server = (CacheServer) entry.getValue().getServerStub();
                try {
                    server.spot(cacheMap);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
