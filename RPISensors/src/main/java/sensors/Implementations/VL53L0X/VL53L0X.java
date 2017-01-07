package sensors.Implementations.VL53L0X;

import dataTypes.TimestampedData1f;
import deviceHardwareAbstractionLayer.Device;
import sensors.interfaces.Ranger;
import sensors.models.SensorPackage;

import java.io.IOException;

/**
 * RPISensors - sensors.Implementations.VL53L0X
 * Created by MAWood on 27/12/2016.
 */
public class VL53L0X extends SensorPackage implements Ranger
{
    VL53L0XRanger sensor;

    /**
     * SensorPackage		- Constructor
     *
     * @param sampleRate
     */
    public VL53L0X(Device i2cImpl, int sampleRate, int sampleSize)
    {
        super(sampleRate, 4);
        sensor = new VL53L0XRanger(i2cImpl, sampleSize);
    }

    @Override
    public void updateData()
    {
        try
        {
            sensor.updateData();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public TimestampedData1f getLatestRange()
    {
        return sensor.getLatestValue();
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
}
