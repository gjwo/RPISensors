package sensors.models;

import java.io.IOException;

import dataTypes.CircularArrayRing;

/**
 * RPISensor - devices.sensors
 * Created by GJWood on 18/07/2016.
 */
public abstract class Sensor <T>
{
    private final CircularArrayRing<T> readings;
    private int sampleRate;
    private int sampleSize;
    private int debugLevel;

    /**
     * Sensor		- Constructor
     * @param sampleSize	- The number of samples that can be held before overwriting
     */
    public Sensor(int sampleSize)
    {
        readings = new CircularArrayRing<>(sampleSize);
        this.sampleRate = sampleRate;
        this.sampleSize = sampleSize;
        this.debugLevel=0;
    }

    // Methods implemented here, shouldn't need overriding
    public T getLatestValue(){return readings.get(0);}
    public T getValue(int i){return readings.get(i);}
    public int getReadingCount(){return readings.size();}
    public void addValue(T value){readings.add(value);}
    public int debugLevel(){return debugLevel;}
    public void setDebugLevel(int l){debugLevel=l;}
    
    // Methods that may need extending by sub classes
    public void printState()
    {
    	System.out.println("readings: "+ readings.size());
     	System.out.print("sampleRate: "+ sampleRate);
    	System.out.print(" sampleSize: "+ sampleSize);
    	System.out.println(" debugLevel: "+ debugLevel);  	  	
    }
    
    // Methods must be implemented but which can't be done here because the types are not known
    public abstract T getAvgValue();
    public abstract T scale(T value);
    public abstract void updateData() throws IOException;

    // Optional Methods
    public void calibrate() throws IOException, InterruptedException{/*if required implement and override in subclass*/}
    public void configure() throws IOException, InterruptedException{/*if required implement and override in subclass*/}
    public void selfTest() throws IOException, InterruptedException{/*if required implement and override in subclass*/}
    public void printRegisters(){/*if required implement and override in subclass*/} 
}