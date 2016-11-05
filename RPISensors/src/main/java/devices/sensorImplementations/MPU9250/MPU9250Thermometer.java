package devices.sensorImplementations.MPU9250;

import java.io.IOException;

import devices.dataTypes.Data1D;
import devices.dataTypes.TimestampedData1D;
import devices.sensors.Sensor;

public class MPU9250Thermometer extends Sensor<TimestampedData1D,Data1D>  
{
	public MPU9250Thermometer(int sampleRate, int sampleSize, MPU9250RegisterOperations ro)
	{
		super(sampleRate, sampleSize, ro);
		this.ro = ro;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public TimestampedData1D getAvgValue()
	{
		//TODO
		return null;
	}

	@Override
	public void updateData() throws IOException 
	{
    	short[] temperature = ro.read16BitRegisters(Registers.TEMP_OUT_H,1);
    	temperature = ro.read16BitRegisters(Registers.TEMP_OUT_H,1);
    	addValue(new TimestampedData1D((float)temperature[0]));
	}

}
