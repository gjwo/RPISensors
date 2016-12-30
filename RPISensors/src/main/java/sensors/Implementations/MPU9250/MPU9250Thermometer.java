package sensors.Implementations.MPU9250;

import java.io.IOException;

import dataTypes.TimestampedData1f;
import sensors.models.Sensor;
/**
 * @author GJWood
 * MPU 9250 Magnetometer sensor
 * Created by G.J.Wood on 1/11/2016
 * Based on MPU9250_MS5637_t3 Basic Example Code by: Kris Winer date: April 1, 2014
 * https://github.com/kriswiner/MPU-9250/blob/master/MPU9250_MS5637_AHRS_t3.ino
 * 
 * This class handles the operation of the Thermometer sensor and is a subclass of Sensor, it provides those methods
 * which are hardware specific to the MPU-9250 such as update
 * This class is independent of the bus implementation, register addressing etc as this is handled by RegisterOperations
 *  
 * Hardware registers controlled by this class
 * 0x41 65 TEMP_OUT_H			- Thermometer Temperature reading
 * 
 * TEMP_degC = ((TEMP_OUT – RoomTemp_Offset)/Temp_Sensitivity) + 21degC
 * Where Temp_degC is the temperature in degrees C measured by the temperature sensor. 
 * TEMP_OUT is the actual output of the temperature sensor.
 */

public class MPU9250Thermometer extends Sensor<TimestampedData1f>
{
    protected MPU9250RegisterOperations ro;
    protected MPU9250 parent;
	public MPU9250Thermometer(int sampleSize, MPU9250RegisterOperations ro, MPU9250 parent)
	{
		super(sampleSize);
		this.ro = ro;
		this.parent = parent;
	}
	
	  /**
	   * Prints the contents of registers used by this class 
	   */
	@Override
	public void printRegisters()
	{
	   	ro.printShort(MPU9250Registers.TEMP_OUT_H);
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
    	short[] temperature = ro.readShorts(MPU9250Registers.TEMP_OUT_H,1);
    	float AdjustedTemp = temperature[0] -969f -9.5f +21f;
    	addValue(new TimestampedData1f(AdjustedTemp));
	}

	@Override
	public TimestampedData1f scale(TimestampedData1f value) {
		// no scaling required
		return value;
	}
}