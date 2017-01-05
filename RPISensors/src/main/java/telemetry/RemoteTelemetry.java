package telemetry;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.Instant;

import dataTypes.Data1f;

public interface RemoteTelemetry extends Remote
{
	Instant getLastUpdateTime() throws RemoteException;
	Data1f getVoltage() throws RemoteException;
	Data1f getCurrent() throws RemoteException;
	Data1f getPower() throws RemoteException;
	double getVelocity() throws RemoteException;
	double getDisplacement() throws RemoteException;
}
