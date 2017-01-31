package mapping;

import dataTypes.TimestampedData2f;
import sensors.interfaces.UpdateListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.Instant;
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
    int getStepsPerRevolution() throws RemoteException;
    TimestampedData2f[] getRawRanges() throws RemoteException;
    public Instant lastUpdated() throws RemoteException;
}
