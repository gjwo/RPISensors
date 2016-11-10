package sensors.models;

import java.io.IOException;

import dataTypes.CircularArrayRing;

/**
 * RPISensor - devices.sensors
 * Created by GJWood on 18/07/2016.
 */
public abstract class Sensor <T,S>
{
    protected final CircularArrayRing<T> vals;
    protected S valBias;
    protected S valScaling;
    protected int sampleRate;
    protected int sampleSize;

    /**
     * Sensor		- Constructor
     * @param sampleRate	- The sample rate per second
     * @param sampleSize	- The number of samples that can be held before overwriting
     */
    public Sensor(int sampleRate, int sampleSize)
    {
        vals = new CircularArrayRing<T>(sampleSize);
        this.sampleRate = sampleRate;
        this.sampleSize = sampleSize;
    }

    // Methods implemented here, shouldn't need overriding
    public T getLatestValue(){return vals.get(0);}
    public T getValue(int i){return vals.get(i);}
    public int getReadingCount(){return vals.size();}
    public void setValBias(S valBias){this.valBias = valBias;}
    public S getValBias(){ return valBias;}
    public void setValScaling(S valScaling){this.valScaling = valScaling;}
    public S getValScaling(){return valScaling;}
    public void addValue(T value){vals.add(value);}

    // Methods must be implemented but which can't be done here because the types are not known
    public abstract T getAvgValue();
    public abstract T OffsetAndScale(T value);
    public abstract void updateData() throws IOException;

    // Optional Methods
    public void calibrate() throws IOException, InterruptedException{/*if required implement and override in subclass*/}
    public void configure() throws IOException, InterruptedException{/*if required implement and override in subclass*/}
    public void selfTest() throws IOException, InterruptedException{/*if required implement and override in subclass*/}
    public void init() throws IOException, InterruptedException{/*if required implement and override in subclass*/}
    public void printRegisters(){/*if required implement and override in subclass*/} 
}