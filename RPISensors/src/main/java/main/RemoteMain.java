package main;

import java.rmi.Remote;
import java.rmi.RemoteException;
public interface RemoteMain extends Remote {
	public enum Subsystem{
		INSTRUMENTS,
		DRIVE_ASSEMBLY
	}
	void start() throws RemoteException;
	void shutdown() throws RemoteException;
	void restartSubsystem(Subsystem s) throws RemoteException;

}
