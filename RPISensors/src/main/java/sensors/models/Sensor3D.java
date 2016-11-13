package sensors.models;

import dataTypes.Data3f;
import dataTypes.TimestampedData3f;
/**
 * RPITank - devices.sensors
 * Created by GJWood on 18/07/2016.
 */
public abstract class Sensor3D extends Sensor<TimestampedData3f,Data3f>
{	
	public Sensor3D(int sampleRate, int sampleSize) {
		super(sampleRate, sampleSize);
	    deviceBias = new Data3f(0f,0f,0f); 		//declared generically in super class
	    deviceScaling = new Data3f(1f,1f,1f); 	//declared generically in super class
	}

	@Override
	public TimestampedData3f scale(TimestampedData3f value)
    {
    		TimestampedData3f scaledValue = value.clone();
            scaledValue.setX(value.getX()*deviceScaling.getX());
            scaledValue.setY(value.getY()*deviceScaling.getY()); 
            scaledValue.setZ(value.getZ()*deviceScaling.getZ()); 
            return scaledValue;
    }

	@Override
	public TimestampedData3f getAvgValue()
    {	
		TimestampedData3f sum = new TimestampedData3f(0,0,0);
		float count = getReadingCount(); // float for division later
    	for(int i = 0; i<count; i++)
    	{
    		sum.setX(getValue(i).getX() + sum.getX());
    		sum.setY(getValue(i).getY() + sum.getY());
    		sum.setZ(getValue(i).getZ() + sum.getZ());
    	}
		return new TimestampedData3f(sum.getX()/count,sum.getY()/count,sum.getZ()/count);
    }
}