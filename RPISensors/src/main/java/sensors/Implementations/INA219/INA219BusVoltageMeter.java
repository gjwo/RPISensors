package sensors.Implementations.INA219;

import dataTypes.TimestampedData1f;
import devices.I2C.RegisterOperations;
import sensors.interfaces.VoltageMeter;
import sensors.models.Sensor1D;

import java.io.IOException;

/**
 * RPISensors - sensors.Implementations.INA219
 * Created by MAWood on 04/01/2017.
 */
public class INA219BusVoltageMeter extends Sensor1D implements VoltageMeter
{
    private final RegisterOperations ro;

    INA219BusVoltageMeter(RegisterOperations ro, int sampleSize)
    {
        super(sampleSize);
        this.ro = ro;
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
        int raw = ((ro.readShort(INA219Registers.BUS_VOLTAGE_MEASURE))>>3)*4;
        this.addValue(new TimestampedData1f((float)raw*0.001f));
    }
}
