package com.zju.dcss;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RemoteBeanContext {
    public static <T extends Remote> void registerBean(int port, String name, T obj){
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(port);
            UnicastRemoteObject.exportObject(obj, 0);
            registry.rebind(name, obj);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static<T extends Remote> Object getBean(String host, int port, String name){
        try {
            return Naming.lookup("rmi://"+host+":"+port+"/"+name);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            return null;
        }
    }
}
