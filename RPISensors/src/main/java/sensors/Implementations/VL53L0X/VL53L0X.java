package sensors.Implementations.VL53L0X;

import dataTypes.TimestampedData1f;
import hardwareAbstractionLayer.Device;
import logging.SystemLog;
import sensors.interfaces.Ranger;
import sensors.models.SensorPackage;

/**
 * RPISensors - sensors.Implementations.VL53L0X
 * Created by MAWood on 27/12/2016.
 */
public class VL53L0X extends SensorPackage implements Ranger
{
    private final VL53L0XRanger sensor;

    /**
     * SensorPackage		- Constructor
     * @param device        - The device to be operated by this object
     * @param sampleRate    - The polling rate
     * @param sampleSize    - The number of samples to be stored
     */
    public VL53L0X(Device device, int sampleRate, int sampleSize)
    {

        super(sampleRate);
        SystemLog.log(this.getClass(), SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"Constructor");
        sensor = new VL53L0XRanger(device, sampleSize);
        SystemLog.log(this.getClass(), SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"Constructor End");
    }

    @Override
    public void updateData()
    {
        sensor.updateData();
    }

    @Override
    public TimestampedData1f getLatestRange()
    {
        try
    {
        return sensor.getLatestValue();
    } catch (IndexOutOfBoundsException ignored)
        {
            return new TimestampedData1f(0);
        }
    }

    @Override
    public TimestampedData1f getAvgRange()
    {
        return sensor.getAvgValue();
    }

    @Override
    public TimestampedData1f getRangeData(int i)
    {
        return sensor.getValue(i);
    }

    @Override
    public int getRangeDataCount()
    {
        return sensor.getReadingCount();
    }
    @Override
    public int getRangingTimeBudget() {return sensor.getRangingTimeBudget();}
}
