package com.zju.dcss;

import java.rmi.RemoteException;

/**
 * 客户端操作代理类 不直接操作cg中的方法 只能操作具体的一台缓存服务器
 */
public class CacheHandler implements Cache{
    private CacheGroup cg = null;
    CacheHandler() {
        cg = new CacheGroup(null);

    }


    public void put(String key, Object value) {
        put(null, key, value);
    }


    public void put(String host, String key, Object value) {
        CacheServer cacheServer = cg.getCacheServer(host);
        if (cacheServer != null) {
            try {
                cacheServer.put(key, value);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    public Object get(String key) {

        return get(null, key);
    }


    public Object get(String host, String key) {
        CacheServer cacheServer = cg.getCacheServer(host);
        Object value = null;
        if (cacheServer != null) {
            try {
                value = cacheServer.get(key);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return value;
    }


    public Object remove(String key) {
        CacheServer cacheServer = cg.getCacheServer(null);
        Object obj = null;
        if(cacheServer!=null) {
            try {
                obj = cacheServer.remove(key);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    public Object remove(String host, String key) {
        return remove(key);
    }

}
