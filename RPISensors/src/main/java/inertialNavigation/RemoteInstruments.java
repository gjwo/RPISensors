package inertialNavigation;

import dataTypes.Data3f;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * NavigationDisplay - inertialNavigation
 * Created by MAWood on 19/12/2016.
 */
public interface RemoteInstruments extends Remote
{
    Data3f getTaitBryanAnglesD() throws RemoteException;
}
