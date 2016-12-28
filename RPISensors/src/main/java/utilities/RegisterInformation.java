package utilities;

import utilities.Register.HWDeviceType;
import sensors.Implementations.VL53L0X.*;

public class RegisterInformation
{
	private static final int deviceTypeCount = HWDeviceType.values().length;
	private final Class[] classes;
	private final HWDeviceType[] deviceTypes;
	
	public RegisterInformation()
	{
		this.classes = new Class[deviceTypeCount];
		deviceTypes = HWDeviceType.values();
	}
	
	public void addDeviceRegisters(HWDeviceType dt, Class rc)
	{
		classes[dt.ordinal()] = rc;
	}
}
