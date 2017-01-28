package mapping;

import dataTypes.TimestampedData1f;
import sensors.interfaces.UpdateListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * RemoteRangeScanner
 * Created by GJWood on 26/01/2017.
 */
public interface RemoteRangeScanner extends Remote
{
    void interrupt() throws RemoteException;
    boolean isFinished() throws RemoteException;
    void run() throws RemoteException;
    void registerInterest(UpdateListener listener) throws RemoteException;
    public int getStepsPerRevolution() throws RemoteException;
    TimestampedData1f[] getRawRanges() throws RemoteException;
    HashMap<Float,TimestampedData1f> getRangeMap() throws RemoteException;
}
