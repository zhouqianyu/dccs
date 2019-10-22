package com.zju.dcss;

/**
 * 文件系统操作实际代理类
 */
public class FileSystemHandler {
    @Delegate(
            interfaceName = "com.zju.dcss.FileSystem",
            methodName = "listFile"
    )
    String listFile(String root){
        return null;
    }
}
