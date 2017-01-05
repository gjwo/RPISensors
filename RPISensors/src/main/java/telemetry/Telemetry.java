package telemetry;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;

import dataTypes.Data1f;
import main.Main;

public class Telemetry implements RemoteTelemetry
{
	private static final String REMOTE_NAME = "Telemetry";
	private Data1f current;
	private Data1f voltage;
	private Data1f power;
	private Instant lastUpdateTime;

	Telemetry()
	{
		voltage = new Data1f(0);
		current = new Data1f(0);
		power = new Data1f(0);
		lastUpdateTime = Instant.now(Main.getMain().getClock());
		
        try
        {
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind(REMOTE_NAME, UnicastRemoteObject.exportObject(this,0));
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
	}
	
	public void updateBatteryData(Data1f v,Data1f i, Data1f p)
	{
		voltage = v;
		current = i;
		power = p;
		lastUpdateTime = Instant.now(Main.getMain().getClock());
	}
	@Override
	public Instant getLastUpdateTime() throws RemoteException {return lastUpdateTime;}

	@Override
	public Data1f getVoltage() throws RemoteException {return voltage;}

	@Override
	public Data1f getCurrent() throws RemoteException {	return current;}

	@Override
	public Data1f getPower() throws RemoteException {return power;}

	@Override
	public double getVelocity() throws RemoteException 	{return 0;}

	@Override
	public double getDisplacement() throws RemoteException {return 0;}
	
	public void shutdown() {unbind();}
	
	void unbind()
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
}