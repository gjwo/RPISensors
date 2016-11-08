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
	    valBias = new Data3f(0,0,0);
	    valScaling= new Data3f(1,1,1);

	}

	@Override
	public TimestampedData3f OffsetAndScale(TimestampedData3f value)
    {
    		TimestampedData3f oSVal = value.clone();
            oSVal.setX(value.getX()*valScaling.getX() -valBias.getX()); //bias will be at current scale?
            oSVal.setY(value.getY()*valScaling.getY() -valBias.getY()); 
            oSVal.setZ(value.getZ()*valScaling.getZ() -valBias.getZ()); 
            return oSVal;
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
