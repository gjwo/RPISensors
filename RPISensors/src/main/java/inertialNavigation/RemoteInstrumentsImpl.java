package inertialNavigation;

import dataTypes.Data3f;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * NavigationDisplay - inertialNavigation
 * Created by MAWood on 19/12/2016.
 */
public class RemoteInstrumentsImpl implements RemoteInstruments
{
    public RemoteInstrumentsImpl(String hostname, int port) throws RemoteException
    {
        try
        {
            Registry reg = LocateRegistry.getRegistry(hostname,port);
            reg.rebind("Instruments", UnicastRemoteObject.exportObject(this,0));
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Data3f getTaitBryanAnglesD() throws RemoteException
    {
        return Instruments.getTaitBryanAnglesD();
    }
}
