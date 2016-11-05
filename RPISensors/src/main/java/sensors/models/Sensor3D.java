package sensors.models;

import dataTypes.Data3D;
import dataTypes.TimestampedData3D;
import sensors.Implementations.MPU9250.MPU9250RegisterOperations;

/**
 * RPITank - devices.sensors
 * Created by GJWood on 18/07/2016.
 */
public abstract class Sensor3D extends Sensor<TimestampedData3D,Data3D>
{	
	public Sensor3D(int sampleRate, int sampleSize, MPU9250RegisterOperations ro) {
		super(sampleRate, sampleSize, ro);
	    valBias = new Data3D(0,0,0);
	    valScaling= new Data3D(1,1,1);

	}

	@Override
	public TimestampedData3D OffsetAndScale(TimestampedData3D value)
    {
    		TimestampedData3D oSVal = value.clone();
            oSVal.setX(value.getX()*valScaling.getX() -valBias.getX()); //bias will be at current scale?
            oSVal.setY(value.getY()*valScaling.getY() -valBias.getY()); 
            oSVal.setZ(value.getZ()*valScaling.getZ() -valBias.getZ()); 
            return oSVal;
    }

	@Override
	public  TimestampedData3D getAvgValue()
    {	
		TimestampedData3D sum = new TimestampedData3D(0,0,0);
		int count = getReadingCount();
    	for(int i = 0; i>count; i++)
    	{
    		sum.setX(getValue(i).getX() + sum.getX());
    		sum.setY(getValue(i).getY() + sum.getY());
    		sum.setZ(getValue(i).getZ() + sum.getZ());
    	}
		TimestampedData3D avg = new TimestampedData3D(sum.getX()/count,sum.getY()/count,sum.getZ()/count);
        return avg;
    }
}
