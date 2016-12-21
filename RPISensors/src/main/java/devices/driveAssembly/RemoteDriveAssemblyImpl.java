package devices.driveAssembly;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RemoteDriveAssemblyImpl implements RemoteDriveAssembly
{

	private final DriveAssembly da;
	private static final String REMOTE_NAME = "DriveAssembly";
	
	public RemoteDriveAssemblyImpl(DriveAssembly da) 
	{
		this.da = da;
        try
        {
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind(REMOTE_NAME, UnicastRemoteObject.exportObject(this,0));
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
	}

	@Override
	public void setSpeed(float speed) throws RemoteException {
		da.setSpeed(speed);
	}

	@Override
	public float getSpeed() throws RemoteException {
		return da.getSpeed();
	}

	@Override
	public void setDirection(float angle) throws RemoteException {
		da.setDirection(angle);
		
	}

	@Override
	public float getDirection() throws RemoteException {
		return da.getDirection();
	}

	@Override
	public void stop() throws RemoteException {
		da.stop();
	}

	public void unbind()
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
