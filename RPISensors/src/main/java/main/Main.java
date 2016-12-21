package main;

import subsystems.SubSystem;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.EnumSet;
import java.util.HashMap;

public class Main implements RemoteMain
{
	private final HashMap<SubSystemType, SubSystem> subSystems;

	public Main(String hostname) throws RemoteException
    {
        System.setProperty("java.rmi.server.hostname", hostname) ;
        Registry reg = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

        reg.rebind("Main", UnicastRemoteObject.exportObject(this,0));

		subSystems = new HashMap<>();
		prepareSubSystems();
	}

    private void prepareSubSystems()
    {
        //TODO: construct and add all subSystems to the hashmap
    }

	@Override
	public void start(EnumSet<SubSystemType> systems) throws RemoteException
	{
        for(SubSystemType systemType:systems) subSystems.get(systemType).start();
	}

	@Override
	public void shutdown(EnumSet<SubSystemType> systems) throws RemoteException
	{
		for(SubSystemType systemType:systems) subSystems.get(systemType).shutdown();
	}

	@Override
	public void restart(EnumSet<SubSystemType> systems) throws RemoteException
	{
		shutdown(systems);
		start(systems);
	}

	@Override
	public void shutdownAll() throws RemoteException
	{
		for(SubSystem system:subSystems.values()) system.shutdown();
	}

	@Override
	public void exit() throws RemoteException
	{
	    shutdownAll();
	    System.exit(0);
	}

	public static void main(String[] args) throws RemoteException
    {
        String hostname = "192.168.1.123";
		new Main(hostname);
	}
}
