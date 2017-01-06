package telemetry;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;
import java.util.HashMap;

import dataTypes.Data1f;
import main.Main;

public class Telemetry implements RemoteTelemetry
{
	private static final String REMOTE_NAME = "Telemetry";
	private double batteryCurrent;
	private double batteryVoltage;
	private double batteryPower;
	private Instant lastUpdateTime;
	private final HashMap<String, DataProviderReal> realProviders;
	private final HashMap<String, DataProviderInt> intProviders;
	private final HashMap<String, DataProviderInstant> instantProviders;

	Telemetry()
	{
		this.realProviders = new HashMap<>();
		this.intProviders = new HashMap<>();
		this.instantProviders = new HashMap<>();
		batteryVoltage = 0;
		batteryCurrent = 0;
		batteryPower = 0;
		lastUpdateTime = Instant.now(Main.getMain().getClock());
		bindProviders();
        bindRegistry();
	}
	
	private void bindProviders()
	{
		// Initialise real providers
        realProviders.put("Volt Meter", () -> batteryVoltage);
        realProviders.put("Current Meter", () -> batteryCurrent);
        realProviders.put("Power Meter", () -> batteryPower);
        
		// Initialise int providers
        
		// Initialise Instant providers

	}
	public void updateBatteryData(Data1f v,Data1f i, Data1f p)
	{
		batteryVoltage = v.getX();
		batteryCurrent = i.getX();
		batteryPower = p.getX();
		lastUpdateTime = Instant.now(Main.getMain().getClock());
	}
	@Override
	public Instant getLastUpdateTime() throws RemoteException {return lastUpdateTime;}
	
	public void shutdown() {unbindRegistry();}
	
	void bindRegistry()
	{
	    try 
	    {
	        Registry reg = LocateRegistry.getRegistry();
	        reg.rebind(REMOTE_NAME, UnicastRemoteObject.exportObject(this,0));
	    } catch (RemoteException e)
	    {
	        e.printStackTrace();
	    }
	}
	void unbindRegistry()
	{
		try
		{
			Registry reg = LocateRegistry.getRegistry();
			reg.unbind(REMOTE_NAME);
		} catch (RemoteException | NotBoundException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public double getRealValue(String name) throws RemoteException
	{
		// TODO Auto-generated method stub
		return realProviders.get(name).getRealValue();
	}
	
	@Override
	public int getIntValue(String name) throws RemoteException
	{
		// TODO Auto-generated method stub
		return intProviders.get(name).getIntValue();
	}
	
	@Override
	public Instant getInstantValue(String name) throws RemoteException
	{
		// TODO Auto-generated method stub
		return instantProviders.get(name).getInstantValue();
	}
}