package com.zju.dcss;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

public class PropHandler implements Serializable {
    private static Properties properties = new Properties();
    private static Map<String, Server> servers = new HashMap<>();
    private static Map<String, Server> caches = new HashMap<>();
    private static Map<String, Server> files = new HashMap<>();
    private static String host = null;
    private static String port = "1099";
    private static String name = null;
    private static String cache_name = null;
    private static String cache_port = "2010";
    private static String diskPersistent = null;
    private static String diskPath = "";
    private static String file_name = "";
    private static String file_port = "";
    private static String file_host = "";
    private static String file_dir = "";
    private static String file_log = "";
    private static String file_data = "";
    private static String file_data_port = "";
    private static int file_replications = 1;

    static {
        try {
            InputStream in = PropHandler.class.getClassLoader().getResourceAsStream("slaves.properties");
            System.out.println();
            properties.load(in);
            String[] names = properties.getProperty("servers.name").split(",");
            for (String name : names) {
                Server server = new Server();
                server.setHost(properties.getProperty("servers." + name + ".host"));
                server.setName(name);
                server.setPort(Integer.parseInt(properties.getProperty("servers." + name + ".port")));
                server.setWorkers(Integer.parseInt(properties.getProperty("servers." + name + ".workers")));
                server.connect();
                servers.put(name, server);
            }
            in = PropHandler.class.getClassLoader().getResourceAsStream("host-env.properties");
            properties.load(in);
            host = properties.getProperty("host");
            port = properties.getProperty("port");
            name = properties.getProperty("name");

            in = PropHandler.class.getClassLoader().getResourceAsStream("cache-servers.properties");
            properties.load(in);
            names = properties.getProperty("servers.name").split(",");
            for (String name : names) {
                Server server = new Server();
                server.setHost(properties.getProperty("servers." + name + ".host"));
                server.setName(name);
                server.setPort(Integer.parseInt(properties.getProperty("servers." + name + ".port")));
                server.setDir(properties.getProperty("dir." + name + ".port"));
                server.connect();
                caches.put(name, server);
            }
            in = PropHandler.class.getClassLoader().getResourceAsStream("cache-config.properties");
            properties.load(in);
            cache_port = properties.getProperty("port");
            cache_name = properties.getProperty("name");
            diskPersistent = properties.getProperty("diskPersistent");
            diskPath = properties.getProperty("diskPath");
            in = PropHandler.class.getClassLoader().getResourceAsStream("file-slaves.properties");
            properties.load(in);
            names = properties.getProperty("servers.name").split(",");
            for (String name : names) {
                Server server = new Server();
                server.setHost(properties.getProperty("servers." + name + ".host"));
                server.setName(name);
                server.setPort(Integer.parseInt(properties.getProperty("servers." + name + ".port")));
                server.setDir(properties.getProperty("servers." + name + ".dir"));
                server.connect();
                files.put(name, server);
            }
            in = PropHandler.class.getClassLoader().getResourceAsStream("file-config.properties");
            properties.load(in);
            file_name = properties.getProperty("name");
            file_port = properties.getProperty("port");
            file_dir = properties.getProperty("dir");
            file_host = properties.getProperty("host");
            file_log = properties.getProperty("log");
            file_data_port = properties.getProperty("file-port");
            file_data = properties.getProperty("data");
            file_replications = Integer.parseInt(properties.getProperty("replications"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Map<String, Server> getSlaves() {
        return servers;
    }

    public static Map<String, Server> getCaches() {
        return caches;
    }

    public static String getHost() {
        return host;
    }

    public static String getPort() {
        return port;
    }

    public static String getName() {
        return name;
    }

    public static String getCache_port() {
        return cache_port;
    }

    public static String getCache_name() {
        return cache_name;
    }

    public static String getDiskPath() {
        return diskPath;
    }

    public static String getDiskPersistent() {
        return diskPersistent;
    }

    public static Map<String, Server> getFiles() {
        return files;
    }

    public static String getFile_name() {
        return file_name;
    }

    public static String getFile_port() {
        return file_port;
    }

    public static String getFile_host() {
        return file_host;
    }

    public static String getFile_dir() {
        return file_dir;
    }

    public static String getFile_log() {
        return file_log;
    }

    public static String getFile_data() {
        return file_data;
    }

    public static String getFile_data_port() {
        return file_data_port;
    }

    public static int getFile_replications() {
        return file_replications;
    }

    public static void main(String[] args) {
        for (Map.Entry<String, Server> e : caches.entrySet()
        ) {
            System.out.println(e.getValue().getName() + e.getValue().getHost() + e.getValue().getPort());
        }
    }
}
