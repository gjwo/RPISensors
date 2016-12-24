package subsystems;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RPISensors - subsystems
 * Created by MAWood on 24/12/2016.
 */
public interface LogDisplayer extends Remote
{
    void showEntry(String entry) throws RemoteException;
}
