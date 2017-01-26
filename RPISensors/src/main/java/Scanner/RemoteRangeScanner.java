package Scanner ;

import dataTypes.TimestampedData1f;
import devices.motors.AngularPositioner;
import sensors.interfaces.Ranger;
import sensors.interfaces.UpdateListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by GJWood on 26/01/2017.
 */
public interface RemoteRangeScanner extends Remote
{
    public void interrupt() throws RemoteException;
    public boolean isFinished() throws RemoteException;
    public void run() throws RemoteException;
    public void registerInterest(UpdateListener listener) throws RemoteException;
    public TimestampedData1f[] getRawRanges() throws RemoteException;
    public HashMap<Float,TimestampedData1f> getRangeMap() throws RemoteException ;
}
