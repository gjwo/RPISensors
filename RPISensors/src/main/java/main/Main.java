package main;

import subsystems.*;
import subsystems.SubSystem.SubSystemType;
import logging.SystemLog;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.EnumSet;
import java.util.HashMap;


public class Main implements RemoteMain
{
	private final HashMap<SubSystemType, SubSystem> subSystems;

	public Main(Registry reg) throws RemoteException
    {
        SystemLog.log(SubSystem.SubSystemType.SUBSYSTEM_MANAGER,SystemLog.LogLevel.TRACE_MAJOR_STATES, "Starting SubSystem manager");
        reg.rebind("Main", UnicastRemoteObject.exportObject(this,0));

		subSystems = new HashMap<>();
		prepareSubSystems();
		SystemLog.log(SubSystem.SubSystemType.SUBSYSTEM_MANAGER,SystemLog.LogLevel.USER_INFORMATION, "System started");
	}

    private void prepareSubSystems()
    {
		SystemLog.log(SubSystem.SubSystemType.SUBSYSTEM_MANAGER,SystemLog.LogLevel.TRACE_MAJOR_STATES, "Preparing subSystems");
        subSystems.put(SubSystemType.DRIVE_ASSEMBLY, new DriveAssemblySubSystem());
        subSystems.put(SubSystemType.INSTRUMENTS, new InstrumentsSubSystem());
        subSystems.put(SubSystemType.TESTING, new TestingSubSystem());
    }

	@Override
	public void start(EnumSet<SubSystemType> systems) throws RemoteException
	{

        for(SubSystemType systemType:systems)
		{
                SystemLog.log(SubSystem.SubSystemType.SUBSYSTEM_MANAGER,SystemLog.LogLevel.TRACE_MAJOR_STATES, "Starting " + systemType.name());
                if(subSystems.get(systemType).getSubSysState() != SubSystemState.RUNNING)
                {
                    subSystems.get(systemType).startup();
                    SystemLog.log(SubSystem.SubSystemType.SUBSYSTEM_MANAGER,SystemLog.LogLevel.TRACE_MAJOR_STATES, "Started " + systemType.name());
                } else SystemLog.log(SubSystem.SubSystemType.SUBSYSTEM_MANAGER,SystemLog.LogLevel.TRACE_MAJOR_STATES, systemType.name() + " already running");

		}
	}

	@Override
	public void shutdown(EnumSet<SubSystemType> systems) throws RemoteException
	{
        for(SubSystemType systemType:systems)
        {
            SystemLog.log(SubSystem.SubSystemType.SUBSYSTEM_MANAGER,SystemLog.LogLevel.TRACE_MAJOR_STATES, "Stopping " + systemType.name());
            if(subSystems.get(systemType).getSubSysState() != SubSystemState.IDLE)
            {
                subSystems.get(systemType).shutdown();
                SystemLog.log(SubSystem.SubSystemType.SUBSYSTEM_MANAGER,SystemLog.LogLevel.TRACE_MAJOR_STATES, "Stopped " + systemType.name());
            } else SystemLog.log(SubSystem.SubSystemType.SUBSYSTEM_MANAGER,SystemLog.LogLevel.TRACE_MAJOR_STATES, systemType.name() + " not running");
        }
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
	public EnumSet<SubSystemType> getSubSystems() throws RemoteException
	{
		return EnumSet.copyOf(subSystems.keySet());
	}

    @Override
    public SubSystemState getSubSystemState(SubSystemType systemType) throws RemoteException
    {
        return subSystems.get(systemType).getSubSysState();
    }

    @Override
	public void exit() throws RemoteException
	{
	    shutdownAll();
	    System.exit(0);
	}

	public static void main(String[] args) throws RemoteException
    {
        if(args.length < 1)
        {
        	SystemLog.log(SubSystem.SubSystemType.SUBSYSTEM_MANAGER,SystemLog.LogLevel.ERROR, "No hostname specified");
            System.err.println("No hostname specified");
            return;
        }
        System.setProperty("java.rmi.server.hostname", args[0]) ;
        Registry reg = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		new Main(reg);
	}
}
