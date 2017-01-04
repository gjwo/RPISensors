package sensors.Implementations.INA219;

import dataTypes.TimestampedData1f;
import devices.I2C.RegisterOperations;
import sensors.models.Sensor1D;

import java.io.IOException;

/**
 * RPISensors - sensors.Implementations.INA219
 * Created by MAWood on 04/01/2017.
 */
public class INA219CurrentMeter extends Sensor1D
{
    private final RegisterOperations ro;

    INA219CurrentMeter(RegisterOperations ro, int sampleSize)
    {
        super(sampleSize);
        this.ro = ro;
    }

    @Override
    public void updateData() throws IOException
    {
        final int ina219_currentDivider_mA = 10;

        int raw = ro.readShort(INA219Registers.CURRENT_MEASURE);
        System.out.println("Raw current: " + raw);
        this.addValue(new TimestampedData1f((float)raw/(float)ina219_currentDivider_mA));
    }
}
