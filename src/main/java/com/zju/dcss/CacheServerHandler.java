package com.zju.dcss;


import java.io.*;
import java.util.Map;

public class CacheServerHandler extends PoolExecutor {
    private CacheMap cacheStorm = null;
    private CacheGroup cg = null;
    boolean diskPersistent = false;
    private String serviceName = "serviceName";

    CacheServerHandler(Server server) {
        this.serviceName = server.getName();
        cacheStorm = new CacheMap();
        cg = new CacheGroup(this.serviceName);
        //TODO...1）从持久化文件读入到Map中
        File directory = new File(server.getDir());

        try {
            if (directory.isDirectory()&&server.isDiskPersistent()) {
                for (File file : directory.listFiles()) {
                    FileInputStream in = new FileInputStream(file);
                    Reader reader = new InputStreamReader(in);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] map = line.split(",");
                        cacheStorm.put(map[0], map[1]);
                    }
                }
            }

            //TODO...2）将自己目前的数据分发给集群中所有存活的服务器
            cg.spot(cacheStorm);
            //TODO... 3）搜索目前的集群中存活的某一台服务器 刷新
            CacheServer cacheServer;
            if ((cacheServer = cg.getCacheServer(null)) != null) {
                cacheStorm.putAll(cacheServer.getAll());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO..4)以一定的时间间隔 将内存数据刷至持久化设备中
        if (server.isDiskPersistent()) {
            execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        FileOutputStream out = new FileOutputStream(directory + "/" + server.getName() + ".data");
                        String str = "";
                        for (Map.Entry<String, Object> entry : cacheStorm.entrySet()
                        ) {
                            str += entry.getKey().toString() + "," + entry.getValue().toString() + "\n";
                        }
                        out.write(str.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 2, 5);
        }

    }

    @Delegate(
            interfaceName = "com.zju.dcss.CacheServer",
            methodName = "put"
    )
    /**
     * 先给自己放进去 然后同步放入其他服务器
     */
    void put(String key, Object value) {
        cacheStorm.put(key, value);
        cg.synchronizedPut(key, value);
    }

    @Delegate(
            interfaceName = "com.zju.dcss.CacheServer",
            methodName = "put"
    )
    /**
     * 收到来自其他缓存服务器的put请求
     */

    void put(String key, Object value, long time) {
        cacheStorm.put(key, value);
    }

    @Delegate(
            interfaceName = "com.zju.dcss.CacheServer",
            methodName = "get"
    )
    Object get(String key) {
        Object obj = null;
        if ((obj = cacheStorm.get(key)) != null) {
            return obj;
        }
        return null;
    }

    @Delegate(
            interfaceName = "com.zju.dcss.CacheServer",
            methodName = "remove"
    )
    Object remove(String key) {
        Object obj = null;
        if ((obj = cacheStorm.get(key)) != null) {
            cacheStorm.remove(key);
        }
        cg.synchronizedRemove(key);
        return obj;
    }

    @Delegate(
            interfaceName = "com.zju.dcss.CacheServer",
            methodName = "getAll"
    )
    CacheMap getAll() {
        return cacheStorm;
    }

    @Delegate(
            interfaceName = "com.zju.dcss.CacheServer",
            methodName = "spot"
    )
    void spot(CacheMap cacheMap) {
        cacheStorm.putAll(cacheMap);
    }

    @Delegate(
            interfaceName = "com.zju.dcss.RemoteServer",
            methodName = "isAlive"
    )
    boolean isAlive() {
        return true;
    }
}
