package com.zju.dcss;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class FileClient {
    private static FileClient fileClient;
    FileSystem fileSystem = null;
    private static String host;
    private static int port;

    static FileClient connect(String host, int port) {
        if (fileClient == null) {
            FileSystem fileSystem = (FileSystem) RemoteBeanContext.getBean(host, port, "file-control");
            fileClient = new FileClient(fileSystem);
            setHost(host);
        }
        return fileClient;
    }

    public static void setHost(String host) {
        FileClient.host = host;
    }

    private FileClient(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public String list(String path) {
        try {
            return fileSystem.list(path);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 写文件逻辑 本地=>master=>slaves 本机先写到master，master写到其他数据节点（master本身也是一个数据节点）
     *
     * @param path
     * @return
     */
    public boolean put(String localPath, String path) {
        try {
            int port = fileSystem.getPutPort();
            SocketHandler socket = new SocketHandler();
            socket.send(localPath, host, port);
            fileSystem.put(path, localPath.substring(localPath.lastIndexOf("/")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
