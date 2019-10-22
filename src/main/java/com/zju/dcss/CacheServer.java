package com.zju.dcss;

import java.rmi.RemoteException;

/**
 * 每个cache服务器的接口操作
 */
public interface CacheServer extends RemoteServer {
    void put(String key, Object value) throws RemoteException;

    void put(String key, Object value, long time) throws RemoteException;

    Object get(String key) throws RemoteException;

    CacheMap getAll() throws RemoteException;

    Object remove(String key) throws RemoteException;

    void spot(CacheMap cacheMap) throws RemoteException;

//    Object isAlive() throws RemoteException;

}
