package main;

import mapping.MappingSubsystem;
import devices.driveAssembly.DriveAssemblySubSystem;
import inertialNavigation.InstrumentsSubSystem;
import subsystems.*;
import subsystems.SubSystem.SubSystemType;
import telemetry.TelemetrySubSystem;
import logging.SystemLog;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;

import dataTypes.NanoClock;


public class Main implements RemoteMain
{
	private static Main main;
	private final HashMap<SubSystemType, SubSystem> subSystems;
	private final NanoClock clock;
	private Main(Registry reg) throws RemoteException
    {
		main = this;
		clock = new NanoClock();
        SystemLog.log(SubSystem.SubSystemType.SUBSYSTEM_MANAGER,SystemLog.LogLevel.TRACE_MAJOR_STATES, "Starting SubSystem manager");
        reg.rebind("Main", UnicastRemoteObject.exportObject(this,0));
		subSystems = new HashMap<>();
		prepareSubSystems();
		SystemLog.log(SubSystem.SubSystemType.SUBSYSTEM_MANAGER,SystemLog.LogLevel.USER_INFORMATION, "System started");
	}
	public NanoClock getClock(){return clock;}
	public static Main getMain() {return main;}
    private void prepareSubSystems()
    {
		SystemLog.log(SubSystem.SubSystemType.SUBSYSTEM_MANAGER,SystemLog.LogLevel.TRACE_MAJOR_STATES, "Preparing subSystems");
        subSystems.put(SubSystemType.DRIVE_ASSEMBLY, new DriveAssemblySubSystem());
        subSystems.put(SubSystemType.INSTRUMENTS, new InstrumentsSubSystem());
        subSystems.put(SubSystemType.TELEMETRY, new TelemetrySubSystem());
		subSystems.put(SubSystemType.MAPPING, new MappingSubsystem());
        subSystems.put(SubSystemType.TESTING, new TestINA219SubSystem());
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
    	try {
            //noinspection ConstantConditions
            System.setProperty("java.rmi.server.hostname", getLocalAddress().getHostAddress());
		}catch (NullPointerException e)
		{
			System.out.println("Failed to get local address");
			System.exit(-1);
		}
        Registry reg = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		new Main(reg);
	}

	private static InetAddress getLocalAddress()
	{
		try
		{
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			// go through all network interfaces
			while(interfaces.hasMoreElements())
			{
				Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses();
				// go through all associated addresses
				while(addresses.hasMoreElements())
				{
					InetAddress address = addresses.nextElement();
					// if the address is not the loopback address and it is IPV4, return it
					if(address instanceof Inet4Address && !address.isLoopbackAddress()) return address;
				}
			}
		} catch (SocketException ignored) {}
		return null;
	}
}
