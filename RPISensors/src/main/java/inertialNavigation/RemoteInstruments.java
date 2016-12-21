package inertialNavigation;

import dataTypes.Data3f;
import dataTypes.TimestampedData3f;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.Instant;
import java.time.ZonedDateTime;

/**
 * NavigationDisplay - inertialNavigation
 * Created by MAWood on 19/12/2016.
 */
public interface RemoteInstruments extends Remote
{
    Data3f getTaitBryanAnglesD() throws RemoteException;
	public Instant getTimestamp() throws RemoteException;
	public ZonedDateTime getDateTime() throws RemoteException;
	public float getYaw() throws RemoteException;
	public float getPitch() throws RemoteException;
	public float getRoll() throws RemoteException;
	public float getHeading() throws RemoteException;
	public float getAttitude() throws RemoteException;
	public float getBank() throws RemoteException;
	public Instant getUpdatedTimestamp() throws RemoteException;
	public Quaternion getQuaternion() throws RemoteException;
	public Data3f getTaitBryanAnglesR() throws RemoteException;
	public TimestampedData3f getMagnetometer() throws RemoteException;
	public TimestampedData3f getAccelerometer() throws RemoteException;
	public TimestampedData3f getGyroscope() throws RemoteException;
	public TimestampedData3f getAngles() throws RemoteException;
	public Data3f getLinearAcceleration() throws RemoteException;
}