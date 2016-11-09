package sensors.models;

import java.io.IOException;

import dataTypes.CircularArrayRing;

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

    public Sensor(int sampleRate, int sampleSize)
    {
        vals = new CircularArrayRing<T>();
        this.sampleRate = sampleRate;
        this.sampleSize = sampleSize;
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

    public S getValBias()
    {
        return valBias;
    }

    public void setValScaling(S valScaling)
    {
        this.valScaling = valScaling;
    }

    public S getValScaling()
    {
        return valScaling;
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
    public void configure() throws IOException, InterruptedException
    {
    	//if required implement and override in subclass
    }
    public void selfTest() throws IOException, InterruptedException
    {
    	//if required implement and override in subclass
    }
    public void init() throws IOException, InterruptedException
    {
    	//if required implement and override in subclass
    }
    public void printRegisters()
    {
    	//if required implement and override in subclass
    }
   
}
