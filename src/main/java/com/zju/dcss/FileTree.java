package com.zju.dcss;

import java.io.*;
import java.sql.Time;
import java.util.*;

public class FileTree {
    public DirectoryItem root = null;
    private String dir = null;
    private String log = null; //存放实时操作log的地址
    private String fileName = null;
    File logFile = null;
    Map<String, DirectoryItem> itemMap = new HashMap<>();//<全路径,item>

    FileTree() {
        this(null);
    }

    /**
     * 如果没有给指定文件名，则默认使用最新的元数据，若从未创建过元数据，则先初始化出根目录 "/"
     *
     * @param fileName
     */
    FileTree(String fileName) {
        dir = PropHandler.getFile_dir();
        log = PropHandler.getFile_log();

        File directory = new File(dir);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (fileName == null && files != null && files.length > 0) {
                this.fileName = files[files.length - 1].getName();//取时间戳最新的文件
            } else if (fileName != null && files != null && files.length > 0) {
                this.fileName = fileName;
            } else {
                root = new DirectoryItem();
                root.setParentPath("-");
                root.setFirstPath("/");
                root.setFullPath("/");

            }
        }
        if (this.fileName != null) {
            try {
                InputStreamReader in = new InputStreamReader(new FileInputStream(dir + "/" + this.fileName));
                BufferedReader reader = new BufferedReader(in);
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] str = line.split("\t");
                    DirectoryItem item = new DirectoryItem();
                    item.setFirstPath(str[0]);
                    item.setFullPath(str[1]);
                    item.setParentPath(str[2]);
                    item.setFile(str[4].equals("f"));
                    String[] positions = str[3].split(" ");
                    item.setPosition(Arrays.asList(positions));
                    itemMap.put(str[1], item);
                }
                root = itemMap.get("/");
                itemMap.remove("/");
                buildTree(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void buildTree(DirectoryItem node) {
        if (node.isFile()) return;
        //扫描临时map中所有parent为该节点的节点，设置为其子节点，并循环向下查找
        for (Map.Entry<String, DirectoryItem> item : itemMap.entrySet()) {
            if (item.getValue().getParentPath().equals(node.getFullPath())) {
                node.getDescendants().put(item.getValue().getFirstPath(), item.getValue());
                buildTree(item.getValue());
            }
        }
    }

    /**
     * 持久化到磁盘上 持久化格式
     * firstPath fullPath parentPath [location1,location2...] isFile
     */
    public void persistTree() {
        Date date = new Date();
        String newFileName = dir + "/meta-" + date.getTime() + ".info";
        File file = new File(newFileName);
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
            persistWrite(root, writer); //递归写出
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void persistWrite(DirectoryItem item, OutputStreamWriter writer) throws IOException {
        if (item != null) {
            writer.append(item.getFirstPath()).append("\t").append(item.getFullPath()).append("\t").
                    append(item.getParentPath()).append("\t");
            for (int i = 0; i < item.getPosition().size(); ++i) {
                writer.append(item.getPosition().get(i));
                if (i != item.getPosition().size() - 1) {
                    writer.append(" ");
                }
            }
            writer.append("\t").append(item.isFile() ? "f" : "d").append("\n");
            writer.flush();
            for (Map.Entry<String, DirectoryItem> node : item.getDescendants().entrySet()) {
                persistWrite(node.getValue(), writer);
            }
        }
        //刷完一次后 需要生成新的log
        Date date = new Date();
        logFile = new File(log + "/log-" + date.getTime() + ".log");
        logFile.createNewFile();
    }

    public void writeLog(String func, String... args) {
        File directory = new File(log);
        File logFile = null;
        try {
            if (directory.isDirectory()) {
                File[] logs = directory.listFiles();
                if (logs != null && logs.length > 0) {
                    logFile = logs[logs.length - 1]; //取最新的log文件
                } else {
                    Date date = new Date();
                    logFile = new File(log + "/log-" + date.getTime() + ".log");
                    logFile.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(logFile);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
                writer.append(func).append("\t");
                for (int i=0;i<args.length;++i){
                    writer.append(args[i]);
                    if(i!=args.length-1) writer.append(" ");
                }
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<DirectoryItem> listFileAndDirectory(String path) {
        DirectoryItem items = scan(path);
        if (items != null) {
            return Arrays.asList(items.getDescendants().values().toArray(new DirectoryItem[0]));
        }
        return null;
    }

    /**
     * 树形搜索所有文件和文件夹,返回目标点
     *
     * @return
     */
    public DirectoryItem scan(String path) {
        String[] strs = path.split("/");
        DirectoryItem item = root; //目前查找到的节点位置
        Map<String, DirectoryItem> descendants = root.getDescendants();//目前查找到节点位置的子节点
        for (int i = 1; i < strs.length; ++i) {
            if ((item = descendants.get(strs[i])) != null) descendants = descendants.get(strs[i]).getDescendants();
            else break;
        }
        return item;
    }

    public boolean mkdir(String path, String newDir) {
        writeLog("mkdir", newDir);
        DirectoryItem item = scan(path);
        if (item == null) return false;
        DirectoryItem newItem = new DirectoryItem();
        newItem.setFirstPath(newDir);
        newItem.setFullPath(path + "/" + newDir);
        newItem.setFile(false);
        newItem.setParentPath(path);
        newItem.setDescendants(null);
        item.getDescendants().put(newDir, newItem);
        return true;
    }

    public int rmdir(String path) {
        writeLog("rmdir", path);
        String[] dirs = path.split("/");
        DirectoryItem item = scan(path);
        //检查是否不存在与是否不空
        if (item == null) return 2;
        if (item.getDescendants().size() > 0) {
            return 1;
        }
        item = scan(path.substring(0, path.lastIndexOf("/")));
        item.getDescendants().remove(dirs[dirs.length - 1]);
        return 0;
    }

    /**
     * 移动文件
     *
     * @param oldPath
     * @param newPath
     * @return
     */
    int mv(String oldPath, String newPath) {
        writeLog("mv", oldPath,newPath);
        if (scan(oldPath) == null) {
            return 1; //无此文件
        }
        DirectoryItem item = scan(newPath);
        if ((item) == null) {
            item = scan(newPath.substring(0, newPath.lastIndexOf("/")));
        }
        if (item == null) {
            return 2; //新目录不存在
        }
        DirectoryItem item2;
        String newPathParent = newPath.substring(0, oldPath.lastIndexOf("/"));
        String oldPathParent = oldPath.substring(0, oldPath.lastIndexOf("/"));
        item2 = scan(oldPathParent);
        String[] strs = oldPath.split("/");
        String[] strs2 = newPath.split("/");
        DirectoryItem tmp = item2.getDescendants().remove(strs[strs.length - 1]);
        tmp.setParentPath(newPathParent);
        item.getDescendants().put(strs2[strs2.length - 1], tmp);
        return 0;
    }

     boolean put(String path, String fileName,String... position){
        DirectoryItem item = scan(path);
        if(item==null){
            return false;
        }else {
            DirectoryItem fileItem = new DirectoryItem();
            fileItem.setParentPath(path);
            fileItem.setFirstPath(fileName);
            fileItem.setFullPath(path+"/"+fileName);
            fileItem.setFile(true);
            //TODO... 根据副本个数分配到不同机器
            fileItem.setPosition(Arrays.asList(position));
            item.getDescendants().put(fileName,fileItem);
        }
        return true;
    }
    public static void main(String[] args) {
        FileTree tree = new FileTree();
//        Map<String ,DirectoryItem> items = tree.root.getDescendants().get("usr").getDescendants();
//        for (Map.Entry<String, DirectoryItem> entry:items.entrySet()) {
//            System.out.println(entry.getKey()+" "+entry.getValue().getFullPath());
//        }
//        List<DirectoryItem> list = tree.listFileAndDirectory("/usr");
//        for (int i = 0;i<list.size();++i){
//            System.out.println(list.get(i).getFullPath());
//        }
    }

}

