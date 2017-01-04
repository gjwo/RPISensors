package sensors.Implementations.INA219;

import dataTypes.TimestampedData1f;
import devices.I2C.I2CImplementation;
import sensors.interfaces.CurrentMeter;
import sensors.models.Sensor1D;

import java.io.IOException;

/**
 * RPISensors - sensors.Implementations.INA219
 * Created by MAWood on 04/01/2017.
 */
public class INA219CurrentMeter extends Sensor1D implements CurrentMeter
{
    private final I2CImplementation i2cImpl;

    INA219CurrentMeter(I2CImplementation i2cImpl, int sampleSize)
    {
        super(sampleSize);
        this.i2cImpl = i2cImpl;
    }

    @Override
    public TimestampedData1f getLatestCurrent()
    {
        return null;
    }

    @Override
    public TimestampedData1f getAvgCurrent()
    {
        return null;
    }

    @Override
    public TimestampedData1f getCurrentData(int i)
    {
        return null;
    }

    @Override
    public int getCurrentDataCount()
    {
        return 0;
    }

    @Override
    public void updateData() throws IOException
    {

    }
}
