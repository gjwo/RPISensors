package telemetry;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.Instant;

@SuppressWarnings("WeakerAccess")
public interface RemoteTelemetry extends Remote
{
	Instant getLastUpdateTime() throws RemoteException;
	double getRealValue(String name) throws RemoteException;
	int getIntValue(String name) throws RemoteException;
	Instant getInstantValue(String name) throws RemoteException;
}