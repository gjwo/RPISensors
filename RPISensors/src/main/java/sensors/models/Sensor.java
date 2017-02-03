package sensors.models;

import dataTypes.CircularArrayRing;
import logging.SystemLog;
import subsystems.SubSystem;

/**
 * RPISensor - devices.sensors
 * Created by GJWood on 18/07/2016.
 */
public abstract class Sensor <T>
{
    private final CircularArrayRing<T> readings;
    private final int sampleSize;

    /**
     * Sensor		- Constructor
     * @param sampleSize	- The number of samples that can be held before overwriting
     */
    protected Sensor(int sampleSize)
    {
        readings = new CircularArrayRing<>(sampleSize);
        this.sampleSize = sampleSize;
    }

    // Methods implemented here, shouldn't need overriding
    public T getLatestValue(){return readings.get(0);}
    public T getValue(int i){return readings.get(i);}
    public int getReadingCount(){return readings.size();}
    protected void addValue(T value){readings.add(value);}

    // Methods that may need extending by sub classes
    void logState()
    {
        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_MAJOR_STATES, "readings: "+ readings.size());
        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_MAJOR_STATES, " sampleSize: "+ sampleSize);
    }
    
    // Methods must be implemented but which can't be done here because the types are not known
    public abstract T getAvgValue();
    public abstract T scale(T value);
    public abstract void updateData();

    // Optional Methods
    public void calibrate() throws InterruptedException{/*if required implement and override in subclass*/}
    public void configure() throws InterruptedException{/*if required implement and override in subclass*/}
    public void selfTest() throws InterruptedException{/*if required implement and override in subclass*/}
    public void printRegisters(){/*if required implement and override in subclass*/} 
}