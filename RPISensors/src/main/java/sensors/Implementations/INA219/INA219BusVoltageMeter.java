package sensors.Implementations.INA219;

import dataTypes.TimestampedData1f;
import devices.I2C.I2CImplementation;
import sensors.interfaces.VoltageMeter;
import sensors.models.Sensor1D;

import java.io.IOException;

/**
 * RPISensors - sensors.Implementations.INA219
 * Created by MAWood on 04/01/2017.
 */
public class INA219BusVoltageMeter extends Sensor1D implements VoltageMeter
{
    private final I2CImplementation i2cImpl;

    INA219BusVoltageMeter(I2CImplementation i2cImpl, int sampleSize)
    {
        super(sampleSize);
        this.i2cImpl = i2cImpl;
    }

    @Override
    public TimestampedData1f getLatestVoltage()
    {
        return null;
    }

    @Override
    public TimestampedData1f getAvgVoltage()
    {
        return null;
    }

    @Override
    public TimestampedData1f getVoltageData(int i)
    {
        return null;
    }

    @Override
    public int getVoltageDataCount()
    {
        return 0;
    }

    @Override
    public void updateData() throws IOException
    {

    }
}
