package com.zju.dcss;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class FileService {
    private static FileTree tree = null;

    static {
        tree = new FileTree();
    }

    @Delegate(
            interfaceName = "com.zju.dcss.FileSystem",
            methodName = "list"
    )
    String list(String path) {
        List<DirectoryItem> items = tree.listFileAndDirectory(path);
        if (items == null) {
            System.err.println("地址错误 请检查");
            System.exit(1);
        }
        String result = "";
        for (DirectoryItem item : items) {
            result += item.getFullPath() + "\n";
        }
        return result;
    }

    @Delegate(
            interfaceName = "com.zju.dcss.FileSystem",
            methodName = "mkdir"
    )
    boolean mkdir(String path, String newDir) {
        if (tree.mkdir(path, newDir)) {
            return true;
        } else {
            System.err.println("地址错误 请检查");
        }
        return false;
    }

    @Delegate(
            interfaceName = "com.zju.dcss.FileSystem",
            methodName = "rmdir"
    )
    boolean rmdir(String path) {
        int status;
        if ((status = tree.rmdir(path)) == 0) {
            return true;
        } else if (status == 1) {
            System.err.println("该文件夹不为空 请检查");

        } else {
            System.err.println("地址错误 请检查");

        }
        return false;
    }

    @Delegate(
            interfaceName = "com.zju.dcss.FileSystem",
            methodName = "getPutPort"
    )
    int getPutPort() {
        return Integer.parseInt(PropHandler.getFile_data_port());
    }

    @Delegate(
            interfaceName = "com.zju.dcss.FileSystem",
            methodName = "put"
    )
    boolean put(String path, String fileName) {
        //TODO..收到请求后 将文件发至其他文件服务器
        Map<String, Server> slaves = PropHandler.getFiles();
        Random random = new Random();
        String[] keys = slaves.keySet().toArray(new String[0]);
        boolean[] visits = new boolean[keys.length];
        SocketHandler handler = new SocketHandler();
        int i = 0;
        do {
            int index = random.nextInt(keys.length);
            if (visits[index]) continue;
            visits[index] = true;
            handler.send(PropHandler.getFile_data() + "/" + fileName,
                    slaves.get(keys[index]).getHost(),
                    slaves.get(keys[index]).getPort());
            ++i;
        } while (i < PropHandler.getFile_replications() - 1); //有一份存在master机上了
        return true;
    }


    public static void main(String[] args) {
        FileService service = new FileService();
//        System.out.println(service.list("/usr"));
//        service.mkdir("/usr", "zhouqianyu");
//        System.out.println(service.list("/usr"));
//        service.rmdir("/usr");
//        System.out.println(service.list("/"));

    }
}
