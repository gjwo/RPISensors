package sensors.models;

import dataTypes.Data3f;
import dataTypes.TimestampedData3f;
/**
 * RPITank - devices.sensors
 * Created by GJWood on 18/07/2016.
 */
public abstract class Sensor3D extends Sensor<TimestampedData3f>
{	
    private Data3f deviceBias; 	//Hardware bias data calculated in calibration
    private Data3f deviceScaling;	//Hardware scale, depends on the scale set up when configuring the device
    private float deviceScalingX;
    private float deviceScalingY;
    private float deviceScalingZ;

    public Sensor3D(int sampleRate, int sampleSize) {
		super(sampleRate, sampleSize);
	    deviceBias = new Data3f(0f,0f,0f); 		//declared generically in super class
	    deviceScaling = new Data3f(1f,1f,1f); 	//declared generically in super class
	}
    public void printState()
    {
    	super.printState();
    	System.out.println("deviceBias: "+ deviceBias.toString());
    	System.out.println("deviceScaling: "+ deviceScaling.toString());
    }

    public void setDeviceBias(Data3f deviceBias){this.deviceBias = deviceBias.clone();}
    public Data3f getDeviceBias(){ return deviceBias;}
    public void setDeviceScaling(Data3f deviceScaling)
    {
    	this.deviceScaling = deviceScaling.clone();
    	deviceScalingX = deviceScaling.getX(); //saved separately for time critical elements
    	deviceScalingY = deviceScaling.getY(); 
    	deviceScalingZ = deviceScaling.getZ(); 

    	}
    public Data3f getDeviceScaling(){return deviceScaling;}

	@Override
	public TimestampedData3f scale(TimestampedData3f value)
    {		//remove cloning to save execution time on critical path 
    		//TimestampedData3f scaledValue = value.clone();
            value.setX(value.getX()*deviceScalingX);
            value.setY(value.getY()*deviceScalingY); 
            value.setZ(value.getZ()*deviceScalingZ); 
            return value;
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