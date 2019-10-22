package com.zju.dcss;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServer extends Remote {
    Object isAlive() throws RemoteException;
}
