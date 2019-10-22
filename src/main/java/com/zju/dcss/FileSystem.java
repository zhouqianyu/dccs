package com.zju.dcss;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 文件系统操作接口
 */
public interface FileSystem extends Remote {
    String list(String root) throws RemoteException;

    String get(String root) throws RemoteException;

    boolean mkdir(String path, String newDir) throws RemoteException;

    boolean rmdir(String path) throws RemoteException;

    int mv(String oldPath, String newPath) throws RemoteException;

    boolean put(String path, String fileName) throws RemoteException;

    int getPutPort() throws RemoteException;

}
