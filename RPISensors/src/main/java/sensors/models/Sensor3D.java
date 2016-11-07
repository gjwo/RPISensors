package sensors.models;

import dataTypes.DataFloat3D;
import dataTypes.TimestampedDataFloat3D;
/**
 * RPITank - devices.sensors
 * Created by GJWood on 18/07/2016.
 */
public abstract class Sensor3D extends Sensor<TimestampedDataFloat3D,DataFloat3D>
{	
	public Sensor3D(int sampleRate, int sampleSize) {
		super(sampleRate, sampleSize);
	    valBias = new DataFloat3D(0,0,0);
	    valScaling= new DataFloat3D(1,1,1);

	}

	@Override
	public TimestampedDataFloat3D OffsetAndScale(TimestampedDataFloat3D value)
    {
    		TimestampedDataFloat3D oSVal = value.clone();
            oSVal.setX(value.getX()*valScaling.getX() -valBias.getX()); //bias will be at current scale?
            oSVal.setY(value.getY()*valScaling.getY() -valBias.getY()); 
            oSVal.setZ(value.getZ()*valScaling.getZ() -valBias.getZ()); 
            return oSVal;
    }

	@Override
	public TimestampedDataFloat3D getAvgValue()
    {	
		TimestampedDataFloat3D sum = new TimestampedDataFloat3D(0,0,0);
		float count = getReadingCount(); // float for division later
    	for(int i = 0; i<count; i++)
    	{
    		sum.setX(getValue(i).getX() + sum.getX());
    		sum.setY(getValue(i).getY() + sum.getY());
    		sum.setZ(getValue(i).getZ() + sum.getZ());
    	}
		return new TimestampedDataFloat3D(sum.getX()/count,sum.getY()/count,sum.getZ()/count);
    }
}
