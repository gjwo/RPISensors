package main;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EnumSet;

public interface RemoteMain extends Remote {
	enum SubSystemType
	{
		INSTRUMENTS,
		DRIVE_ASSEMBLY
	}
	void start(EnumSet<SubSystemType> systems) throws RemoteException;
	void shutdown(EnumSet<SubSystemType> systems) throws RemoteException;
	void restart(EnumSet<SubSystemType> systems) throws RemoteException;
	void shutdownAll() throws RemoteException;
	void exit() throws RemoteException;

}
