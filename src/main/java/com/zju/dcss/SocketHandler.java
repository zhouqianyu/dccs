package com.zju.dcss;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketHandler {
    public void send(String localPath,String host,int port){
        try {
            File file = new File(localPath);
            if (file.exists()) {
                FileInputStream in = new FileInputStream(file);
                Socket socket = new Socket(host, port);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(file.getName());
                out.writeLong(file.length());
                // 开始传输文件
                System.out.println("======== 开始传输文件 ========");
                byte[] bytes = new byte[4096];
                int length = 0;
                long progress = 0;
                while ((length = in.read(bytes, 0, bytes.length)) != -1) {
                    out.write(bytes, 0, length);
                    out.flush();
                    progress += length;
                    System.out.print("| " + (100 * progress / file.length()) + "% |"); //进度显示
                }
                System.out.println();
                System.out.println("======== 文件传输成功 ========");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
