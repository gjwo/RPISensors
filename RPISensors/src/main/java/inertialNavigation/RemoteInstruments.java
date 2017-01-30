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
@SuppressWarnings("WeakerAccess")
public interface RemoteInstruments extends Remote
{
	TimestampedData3f getTaitBryanAnglesD() throws RemoteException;
	Instant getTimestamp() throws RemoteException;
	ZonedDateTime getDateTime() throws RemoteException;
	float getYaw() throws RemoteException;
	float getPitch() throws RemoteException;
	float getRoll() throws RemoteException;
	float getHeading() throws RemoteException;
	float getAttitude() throws RemoteException;
	float getBank() throws RemoteException;
	Instant getUpdatedTimestamp() throws RemoteException;
	Quaternion getQuaternion() throws RemoteException;
	TimestampedData3f getTaitBryanAnglesR() throws RemoteException;
	TimestampedData3f getMagnetometer() throws RemoteException;
	TimestampedData3f getAccelerometer() throws RemoteException;
	TimestampedData3f getGyroscope() throws RemoteException;
	TimestampedData3f getAngles() throws RemoteException;
	TimestampedData3f getLinearAcceleration() throws RemoteException;
	TimestampedData3f getEulerAnglesR() throws RemoteException;
	TimestampedData3f getEulerAnglesD() throws RemoteException;

}