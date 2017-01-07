package sensors.models;

import dataTypes.Data1f;
import dataTypes.TimestampedData1f;

/**
 * RPISensors - sensors.models
 * Created by MAWood on 27/12/2016.
 */
public abstract class Sensor1D extends Sensor<TimestampedData1f>
{
    private Data1f deviceBias; 	//Hardware bias data calculated in calibration
    private Data1f deviceScaling;	//Hardware scale, depends on the scale set up when configuring the device
    private float deviceScalingX;

    protected Sensor1D(int sampleSize) {
        super(sampleSize);
        deviceBias = new Data1f(0f); 		//declared generically in super class
        deviceScaling = new Data1f(1f); 	//declared generically in super class
    }
    public void logState()
    {
        super.logState();
        System.out.println("deviceBias: "+ deviceBias.toString());
        System.out.println("deviceScaling: "+ deviceScaling.toString());
    }

    public void setDeviceBias(Data1f deviceBias){this.deviceBias = deviceBias.clone();}
    public Data1f getDeviceBias(){ return deviceBias;}
    public void setDeviceScaling(Data1f deviceScaling)
    {
        this.deviceScaling = deviceScaling.clone();
        deviceScalingX = deviceScaling.getX(); //saved separately for time critical elements

    }
    public Data1f getDeviceScaling(){return deviceScaling;}

    @Override
    public TimestampedData1f scale(TimestampedData1f value)
    {		//remove cloning to save execution time on critical path
        //TimestampedData3f scaledValue = value.clone();
        value.setX(value.getX()*deviceScalingX);
        return value;
    }

    @Override
    public TimestampedData1f getAvgValue()
    {
        TimestampedData1f sum = new TimestampedData1f(0);
        float count = getReadingCount(); // float for division later
        for(int i = 0; i<count; i++)
        {
            sum.setX(getValue(i).getX() + sum.getX());
        }
        return new TimestampedData1f(sum.getX()/count);
    }
}
