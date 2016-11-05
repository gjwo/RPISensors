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
		TimestampedData1D sum = new TimestampedData1D(0);
		int count = getReadingCount();
    	for(int i = 0; i>count; i++)
    	{
    		sum.setX(getValue(i).getX() + sum.getX());
    	}
		TimestampedData1D avg = new TimestampedData1D(sum.getX()/count);
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
