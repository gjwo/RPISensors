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
import subsystems.SubSystem.SubSystemType;

public class Telemetry implements RemoteTelemetry
{
	private class MapValuesReal
	{
		final DataProviderReal 	dp;
		final int 				dataIndex;
		final SubSystemType 	sst;
		MapValuesReal(DataProviderReal dp,int dataIndex,	SubSystemType sst)
		{
			this.dp = dp;
			this.dataIndex = dataIndex;
			this.sst = sst;
		}
	}
	
	private static final String REMOTE_NAME = "Telemetry";
	private Instant lastUpdateTime;
	private final HashMap<String, MapValuesReal> realProviders;
	private final HashMap<String, DataProviderInt> intProviders;
	private final HashMap<String, DataProviderInstant> instantProviders;
	private final double[] reals;
	private final int[] ints;
	private final Instant[] instants;

	Telemetry()
	{
		// TODO generalise storage of values see method below
		// add an int array index to each DataProvider map/hashtable,
		// create storage arrays for each class of data
		// size the arrays based on the number of bound providers
		// populate the arrays with the current values in the appropriate indexed element
		// generalise the update routines using an index parameter instead of variable group name
		// remove all specific class variables apart form lasUpdateTime
		
		// TODO generalise to deal with multiple subsystems see method below
		// Add a subsystem type to the hashtable.
		
		// TODO (stretch!) allow subystems to dynamically bind and unbind new data providers
		
		this.realProviders = new HashMap<>();
		this.intProviders = new HashMap<>();
		this.instantProviders = new HashMap<>();
		lastUpdateTime = Instant.now(Main.getMain().getClock());
		bindProviders();
		reals = new double[realProviders.size()];
		ints = new int[intProviders.size()];
		instants = new Instant[intProviders.size()];
        bindRegistry();
	}
	
	private void bindProviders()
	{
		// Initialise real providers
        realProviders.put("Volt Meter", 	new MapValuesReal(() -> reals[0],0,SubSystemType.TELEMETRY));
        realProviders.put("Current Meter", 	new MapValuesReal(() -> reals[1],1,SubSystemType.TELEMETRY));
        realProviders.put("Power Meter",	new MapValuesReal(() -> reals[2],2,SubSystemType.TELEMETRY));
        //realProviders.put("Volt Meter", () -> batteryVoltage);
        //realProviders.put("Current Meter", () -> batteryCurrent);
        //realProviders.put("Power Meter", () -> batteryPower);
       
		// Initialise int providers
        
		// Initialise Instant providers

	}
	public void updateBatteryData(Data1f v,Data1f i, Data1f p)
	{
		reals[0] = v.getX();
		reals[1] = i.getX();
		reals[2] = p.getX();
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
		// return will be null if key not found
		return reals[realProviders.get(name).dataIndex];
	}
	
	@Override
	public int getIntValue(String name) throws RemoteException
	{
		// return will be null if key not found
		return intProviders.get(name).getIntValue();
	}
	
	@Override
	public Instant getInstantValue(String name) throws RemoteException
	{
		// return will be null if key not found
		return instantProviders.get(name).getInstantValue();
	}
}