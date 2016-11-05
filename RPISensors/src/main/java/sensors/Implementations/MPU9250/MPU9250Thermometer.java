package sensors.Implementations.MPU9250;

import java.io.IOException;

import dataTypes.Data1D;
import dataTypes.TimestampedData1D;
import sensors.models.Sensor;

public class MPU9250Thermometer extends Sensor<TimestampedData1D,Data1D>  
{
	public MPU9250Thermometer(int sampleRate, int sampleSize, MPU9250RegisterOperations ro)
	{
		super(sampleRate, sampleSize, ro);
		this.ro = ro;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public  TimestampedData1D getAvgValue()
    {	
		double sum = 0;
		int count = getReadingCount();
    	for(int i = 0; i<count; i++)
    	{
    		sum +=getValue(i).getX();
    	}
		TimestampedData1D avg = getLatestValue().clone();
		avg.setX((float)(sum/(float)count));
        return avg;
    }

	@Override
	public void updateData() throws IOException 
	{
    	short[] temperature = ro.read16BitRegisters(Registers.TEMP_OUT_H,1);
    	temperature = ro.read16BitRegisters(Registers.TEMP_OUT_H,1);
    	addValue(new TimestampedData1D((float)temperature[0]));
	}

}
