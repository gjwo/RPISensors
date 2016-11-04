package devices.sensors;

import java.io.IOException;
import java.util.Set;

import devices.dataTypes.CircularArrayRing;
import devices.sensorImplementations.MPU9250.MPU9250RegisterOperations;

/**
 * RPITank - devices.sensors
 * Created by GJWood on 18/07/2016.
 */
public abstract class Sensor <T,S>
{
    protected final CircularArrayRing<T> vals;
    protected S valBias;
    protected S valScaling;
    protected int sampleRate;
    protected int sampleSize;
    protected MPU9250RegisterOperations ro;

    public Sensor(int sampleRate, int sampleSize, MPU9250RegisterOperations ro)
    {
        vals = new CircularArrayRing<T>();
        valBias = null;
        valScaling = null;
        this.sampleRate = sampleRate;
        this.sampleSize = sampleSize;
        this.ro = ro;
    }

    public T getLatestValue()
    {
        return vals.get(0);
    }
    public T getAvgValue()
    {	//TODO
        return vals.get(0);
    }

    public T getValue(int i)
    {
        return vals.get(i);
    }

    public int getReadingCount()
    {
        return vals.size();
    }

    public void setValBias(S valBias)
    {
        this.valBias = valBias;
    }

    public void setValScaling(S valScaling)
    {
        this.valScaling = valScaling;
    }

    public void addValue(T value)
    {
    	//TODO fix scaling etc
        //value.scale(valScaling);
        //value.offset(valBias);
        vals.add(value);
    }
    public void updateData() throws IOException
    {
    	
    }
    public void calibrate()
    {
    	
    }
    public void selfTest()
    {
    	
    }
    public void init()
    {
    	
    }
}
