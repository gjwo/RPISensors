package sensors.models;

import java.io.IOException;

import dataTypes.CircularArrayRing;
import sensors.Implementations.MPU9250.MPU9250RegisterOperations;

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
        this.sampleRate = sampleRate;
        this.sampleSize = sampleSize;
        this.ro = ro;
    }

    public T getLatestValue()
    {
        return vals.get(0);
    }
    public T getAvgValue()
    {	
    	//implement and override in subclass
    	System.out.println("ERROR: OffsetAndScale not overridden");
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
        vals.add(value);
    }
 
    public T OffsetAndScale(T value)
    {
    	T oSVal = null;
    	//implement and override in subclass
    	System.out.println("ERROR: OffsetAndScale not overridden");
        return oSVal;
    }

    public void updateData() throws IOException
    {
    	//implement and override in subclass
    	System.out.println("ERROR: updateData not overridden");
   }
    public void calibrate() throws IOException, InterruptedException
    {
    	//if required implement and override in subclass
    }
    public void selfTest() throws IOException
    {
    	//if required implement and override in subclass
    }
    public void init() throws InterruptedException, IOException
    {
    	//if required implement and override in subclass
    }
}
