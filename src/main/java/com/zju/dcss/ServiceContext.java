package com.zju.dcss;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class ServiceContext {
    public static void startService() {
        Worker worker = (Worker) DelegateHandler.bind(new Class[]{Worker.class}, new WorkerDelegate());
        RemoteBeanContext.registerBean(Integer.parseInt(PropHandler.getPort()), PropHandler.getName(), worker);
    }

    public static void startCacheService() {
        String[] ports = PropHandler.getCache_port().split(",");
        String[] name = PropHandler.getCache_name().split(",");
        String[] diskPath = PropHandler.getDiskPath().split(",");
        String[] isDiskPersistent = PropHandler.getDiskPersistent().split(",");
        for (int i = 0; i < name.length; i++) {
            Server server = new Server();
            server.setPort(Integer.parseInt(ports[i]));
            server.setName(name[i]);
            server.setDir(diskPath[i]);
            server.setDiskPersistent(Boolean.parseBoolean(isDiskPersistent[i]));
            CacheServer cacheServer = (CacheServer) DelegateHandler.bind(new Class[]{CacheServer.class},
                    new CacheServerHandler(server));
            RemoteBeanContext.registerBean(Integer.parseInt(ports[i]), name[i], cacheServer);
        }
    }

    public static void startFileMasterService() {
        FileSystem system = (FileSystem) DelegateHandler.bind(new Class[]{FileSystem.class}, new FileService());
        RemoteBeanContext.registerBean(Integer.parseInt(PropHandler.getFile_port()),
                "file-control", system);
        //Master服务器一旦启动就准备接受数据
        ServerSocket socketServer = null;
        try {
            socketServer = new ServerSocket(Integer.parseInt(PropHandler.getFile_data_port()));
            while (true) {
                // server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
                Socket socket = socketServer.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DataInputStream in = new DataInputStream(socket.getInputStream());
                            String fileName = in.readUTF();
                            File file = new File(PropHandler.getFile_data() + "/" + fileName);
                            FileOutputStream out = new FileOutputStream(file);
                            byte[] bytes = new byte[4096];
                            int length = 0;
                            while ((length = in.read(bytes, 0, bytes.length)) != -1) {
                                out.write(bytes, 0, length);
                                out.flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * 我们的服务端处理客户端的连接请求是同步进行的， 每次接收到来自客户端的连接请求后，
         * 都要先跟当前的客户端通信完之后才能再处理下一个连接请求。 这在并发比较多的情况下会严重影响程序的性能，
         * 为此，我们可以把它改为如下这种异步处理与客户端通信的方式
         */
        // 每接收到一个Socket就建立一个新的线程来处理它


    }

    public static void startFileSlaveService() {
        //TODO..这里是临时写法 现在在localhost进行测试，所以可以直接读取slaves的配置，多机部署时 代码还是要调整为读file-config
        Map<String, Server> fileServers = PropHandler.getFiles();
        for (Map.Entry<String, Server> entry : fileServers.entrySet()) {
            ServerSocket socketServer = null;
            System.out.println(entry.getValue().getPort());
            try {
                socketServer = new ServerSocket(entry.getValue().getPort());
//                while (true) {
                    // server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
                    Socket socket = socketServer.accept();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DataInputStream in = new DataInputStream(socket.getInputStream());
                                String fileName = in.readUTF();
                                File file = new File(entry.getValue().getDir() + "/" + fileName);
                                FileOutputStream out = new FileOutputStream(file);
                                byte[] bytes = new byte[4096];
                                int length = 0;
                                while ((length = in.read(bytes, 0, bytes.length)) != -1) {
                                    out.write(bytes, 0, length);
                                    out.flush();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
//        startCacheService();
//        startService();
        startFileMasterService();
    }
}
