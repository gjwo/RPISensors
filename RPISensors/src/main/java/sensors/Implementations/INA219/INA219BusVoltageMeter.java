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
public class INA219BusVoltageMeter extends Sensor1D
{
    private final RegisterOperations ro;

    INA219BusVoltageMeter(RegisterOperations ro, int sampleSize)
    {
        super(sampleSize);
        this.ro = ro;
    }

    @Override
    public void updateData() throws IOException
    {
        int raw = ((ro.readShort(INA219Registers.BUS_VOLTAGE_MEASURE))>>3)*4;
        this.addValue(new TimestampedData1f((float)raw*0.001f));
    }
}
