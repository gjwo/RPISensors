package devices.driveAssembly;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteDriveAssembly extends Remote
{
    void setSpeed(float speed) throws RemoteException;
    float getSpeed() throws RemoteException;
    void setDirection(float angle) throws RemoteException;
    float getDirection() throws RemoteException;

    void stop() throws RemoteException;
}
