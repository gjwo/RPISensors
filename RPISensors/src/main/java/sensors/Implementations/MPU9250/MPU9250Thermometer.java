package sensors.Implementations.MPU9250;

import java.io.IOException;

import dataTypes.Data1f;
import dataTypes.TimestampedData1f;
import sensors.models.NineDOF;
import sensors.models.Sensor;

public class MPU9250Thermometer extends Sensor<TimestampedData1f,Data1f>  
{
    protected MPU9250RegisterOperations ro;
    protected NineDOF parent;
	public MPU9250Thermometer(int sampleRate, int sampleSize, MPU9250RegisterOperations ro, NineDOF parent)
	{
		super(sampleRate, sampleSize);
		this.ro = ro;
		this.parent = parent;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public  TimestampedData1f getAvgValue()
    {	
		double sum = 0;
		int count = getReadingCount();
    	for(int i = 0; i<count; i++)
    	{
    		sum +=getValue(i).getX();
    	}
		TimestampedData1f avg = getLatestValue().clone();
		avg.setX((float)(sum/(float)count));
        return avg;
    }

	@Override
	public void updateData() throws IOException 
	{
		
		//TEMP_degC = ((TEMP_OUT – RoomTemp_Offset)/Temp_Sensitivity) + 21degC
		
    	short[] temperature = ro.read16BitRegisters(Registers.TEMP_OUT_H,1);
    	temperature = ro.read16BitRegisters(Registers.TEMP_OUT_H,1);
    	addValue(new TimestampedData1f((float)temperature[0]));
	}

}
