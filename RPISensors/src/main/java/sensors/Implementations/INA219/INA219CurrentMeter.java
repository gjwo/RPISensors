package sensors.Implementations.INA219;

import dataTypes.TimestampedData1f;
import devices.I2C.RegisterOperations;
import sensors.interfaces.CurrentMeter;
import sensors.models.Sensor1D;

import java.io.IOException;

/**
 * RPISensors - sensors.Implementations.INA219
 * Created by MAWood on 04/01/2017.
 */
public class INA219CurrentMeter extends Sensor1D implements CurrentMeter
{
    private final RegisterOperations ro;

    INA219CurrentMeter(RegisterOperations ro, int sampleSize)
    {
        super(sampleSize);
        this.ro = ro;
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
        final int ina219_currentDivider_mA = 10;
        int raw = ro.readShort(INA219Registers.CURRENT_MEASURE);
        this.addValue(new TimestampedData1f((float)raw/(float)ina219_currentDivider_mA));
    }
}
